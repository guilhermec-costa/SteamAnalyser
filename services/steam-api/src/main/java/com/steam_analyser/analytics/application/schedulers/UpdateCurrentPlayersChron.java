package com.steam_analyser.analytics.application.schedulers;

import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;
import com.steam_analyser.analytics.infra.mediator.Mediator;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.lang.Math;
import java.time.Duration;

import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdateCurrentPlayersChron implements ISteamChron {

  private final SteamAppService steamAppService;
  private final SteamAppStatsService steamAppStatsService;
  private final Executor taskExecutor;
  private final Mediator mediator;
  private final TaskScheduler taskScheduler;
  private SteamConfiguration steamConfiguration;
  private final Duration executionFrequency = Duration.ofMinutes(30);
  private final int retryLimit = 4;
  private final int initialBackoff = 500;
  private final int batchSize = 250;

  @Override
  public void start(final SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    taskScheduler.scheduleAtFixedRate(this::run, executionFrequency);
    log.info("Executing task: \"" + getChronName() + "\"");
  }

  @Override
  public void run() {
    System.out.println("aqui");
    var steamAppsList = steamAppService.findAllSteamApps();
    var batches = partitionList(steamAppsList, processBatchSizeFor(steamAppsList));

    List<CompletableFuture<Void>> futures = batches.stream()
        .map(this::processBatchParallely)
        .collect(Collectors.toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    log.info("Finishing execution of \"" + getChronName() + "\"");
  }

  private CompletableFuture<Void> processBatchParallely(List<SteamAppModel> batch) {
    return CompletableFuture.runAsync(() -> {
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

      steamAppStatsService.saveMultiple(statsToSave);
      propagateSideEffects(toBePropagated);
    }, taskExecutor);
  }

  private Integer retryQueryPlayerCountForApp(String steamAppId) {
    int attempts = 0;
    int backoff = initialBackoff;

    while (attempts < retryLimit) {
      try {
        Map<String, String> args = new HashMap<>();
        args.put("appid", steamAppId);

        Thread.sleep(850);
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
      attempts++;
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
