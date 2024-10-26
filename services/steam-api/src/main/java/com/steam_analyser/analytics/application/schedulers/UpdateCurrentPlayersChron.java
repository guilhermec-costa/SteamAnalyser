package com.steam_analyser.analytics.application.schedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;
import com.steam_analyser.analytics.infra.mediator.Mediator;
import com.steam_analyser.analytics.util.ThreadUtil;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.lang.Math;
import java.time.Duration;

import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.*;

@Component
@Slf4j
public class UpdateCurrentPlayersChron implements ISteamChron {

  private SteamAppService steamAppService;
  private SteamAppStatsService steamAppStatsService;
  private Mediator mediator;
  private TaskScheduler taskScheduler;
  private ProfillingService profillingService;
  private final Executor executor = Executors.newFixedThreadPool(100);
  private int batchProcessingCounter = 0;

  public UpdateCurrentPlayersChron(
      SteamAppService steamAppService,
      SteamAppStatsService steamAppStatsService,
      Mediator mediator,
      @Qualifier("sharedTaskScheduler") TaskScheduler taskScheduler,
      ProfillingService profillingService) {
    this.steamAppService = steamAppService;
    this.steamAppStatsService = steamAppStatsService;
    this.mediator = mediator;
    this.taskScheduler = taskScheduler;
    this.profillingService = profillingService;
  }

  private SteamConfiguration steamConfiguration;
  private final Duration executionFrequency = Duration.ofMinutes(35);
  private final int retryLimit = 3;
  private final int initialBackoff = 500;
  private final int batchSize = 400;

  @Override
  public void start(final SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    System.out.println(Runtime.getRuntime().availableProcessors());
    taskScheduler.scheduleAtFixedRate(this::run, executionFrequency);
    log.info("Executing task: \"" + getChronName() + "\"");
  }

  @Override
  public void run() {
    var steamAppsList = steamAppService.findNSteamApps(PageRequest.of(0, 1500));
    var batches = partitionList(steamAppsList, processBatchSizeFor(steamAppsList));

    var start = profillingService.getNow();
    List<CompletableFuture<Void>> futures = batches.stream()
        .map(this::processBatchParallely)
        .collect(Collectors.toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  private CompletableFuture<Void> processBatchParallely(List<SteamAppModel> batch) {
    return CompletableFuture.runAsync(() -> {
      batchProcessingCounter++;
      log.info("Processing batch id: " + batchProcessingCounter);
      List<PartialSteamAppStatsHistory> toBePropagated = new ArrayList<>();
      List<SteamAppStatsModel> statsToSave = new ArrayList<>();

      for (var nextApp : batch) {
        SteamAppStatsModel actUponAppStats = steamAppStatsService.findOrCreateStatsModelInstance(nextApp);
        Integer playerCount = retryQueryPlayerCountForApp(nextApp.getSteamAppId());

        if (playerCount != null) {
          actUponAppStats.updateCurrentPlayers(playerCount);
          statsToSave.add(actUponAppStats);
          toBePropagated.add(new PartialSteamAppStatsHistory(nextApp, playerCount, getExecutionDate()));
        }
      }

      CompletableFuture<Void> saveFuture = CompletableFuture
          .runAsync(() -> steamAppStatsService.saveMultiple(statsToSave), executor);
      CompletableFuture<Void> propagateFuture = CompletableFuture.runAsync(() -> propagateSideEffects(toBePropagated),
          executor);

      CompletableFuture.allOf(saveFuture, propagateFuture).join();

    }, executor);
  }

  private Integer retryQueryPlayerCountForApp(String steamAppId) {
    int currentAttempts = 0;
    int backoff = initialBackoff;

    while (currentAttempts < retryLimit) {
      try {
        Map<String, String> args = new HashMap<>();
        args.put("appid", steamAppId);

        var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string)
            .call("GET", GetNumberOfCurrentPlayers.string, GetNumberOfCurrentPlayers.version, args);

        return extractPlayerCountingFromResponse(currentPlayersResponse);

      } catch (Exception e) {
        try {
          Thread.sleep(backoff);
          backoff *= 2;
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return null;
        }
      }
      currentAttempts++;
    }

    return null;
  }

  private Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  private void propagateSideEffects(List<PartialSteamAppStatsHistory> eventArgs) {
    PlayerCountUpdatedEvent updateCountEvent = new PlayerCountUpdatedEvent(eventArgs);
    mediator.publish(updateCountEvent);
  }

  private <T> int processBatchSizeFor(List<T> items) {
    return batchSize;
  }

  private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
    log.info("Partioning app list");
    var partitionContainer = new ArrayList<List<T>>();
    int itemsCount = list.size();

    for (int i = 0; i < itemsCount; i += batchSize) {
      int nextPartitionLimit = Math.min(i + batchSize, itemsCount);
      var singlePartition = list.subList(i, nextPartitionLimit);
      partitionContainer.add(singlePartition);
    }
    return partitionContainer;
  }
}
