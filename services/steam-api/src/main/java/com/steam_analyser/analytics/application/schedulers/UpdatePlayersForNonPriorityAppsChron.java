package com.steam_analyser.analytics.application.schedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountBatchUpdatedEvent;
import com.steam_analyser.analytics.application.events.PlayerCountUnitUpdatedEvent;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;
import com.steam_analyser.analytics.infra.mediator.Mediator;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
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
public class UpdatePlayersForNonPriorityAppsChron implements ISteamChron {

  private SteamAppService steamAppService;
  private SteamAppStatsService steamAppStatsService;
  private Mediator mediator;
  private TaskScheduler taskScheduler;
  private SchedulerManager schedulerManager;
  private ProfillingService profillingService;
  private Executor executor;

  public UpdatePlayersForNonPriorityAppsChron(
      SteamAppService steamAppService,
      SteamAppStatsService steamAppStatsService,
      Mediator mediator,
      ProfillingService profillingService,
      SchedulerManager schedulerManager) {
    this.steamAppService = steamAppService;
    this.steamAppStatsService = steamAppStatsService;
    this.mediator = mediator;
    this.profillingService = profillingService;
    executor = Executors.newFixedThreadPool(48);
    this.schedulerManager = schedulerManager;
  }

  private SteamConfiguration steamConfiguration;
  private final Duration executionFrequency = Duration.ofMinutes(90);
  private final int retryLimit = 3;
  private final int initialBackoff = 500;
  private final int batchSize = 250;

  @Override
  public void start(final SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    schedulerManager.scheduleChronIfAllowed(this);
  }

  @Override
  public void run() {
    var steamAppsList = steamAppService.findAllSteamApps();
    var start = profillingService.getNow();

    List<CompletableFuture<Void>> updateFutures = steamAppsList.stream().map(this::processAppUnitPararelly)
        .collect(Collectors.toList());

    CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0])).join();
    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  private CompletableFuture<Void> processAppUnitPararelly(SteamAppModel app) {
    return CompletableFuture.runAsync(() -> {
      SteamAppStatsModel actUponAppStats = steamAppStatsService.findOrCreateStatsModelInstance(app);
      Integer newPlayerCount = retryQueryPlayerCountForApp(app.getSteamAppId());

      if (steamAppStatsService.canUpdateAppStatsPlayerCount(actUponAppStats, newPlayerCount)) {
        actUponAppStats.updateCurrentPlayers(newPlayerCount);
        steamAppStatsService.saveOne(actUponAppStats);
        var sideEffectArg = new PartialSteamAppStatsHistory(app, newPlayerCount, getExecutionDate());
        mediator.publish(new PlayerCountUnitUpdatedEvent(sideEffectArg));
      }
    }, executor);
  }

  private CompletableFuture<Void> processBatchParallely(List<SteamAppModel> batch) {
    return CompletableFuture.runAsync(() -> {
      List<PartialSteamAppStatsHistory> toBePropagated = new ArrayList<>();
      List<SteamAppStatsModel> statsToSave = new ArrayList<>();

      for (var nextApp : batch) {
        SteamAppStatsModel actUponAppStats = steamAppStatsService.findOrCreateStatsModelInstance(nextApp);
        Integer playerCount = retryQueryPlayerCountForApp(nextApp.getSteamAppId());

        if (steamAppStatsService.canUpdateAppStatsPlayerCount(actUponAppStats, playerCount)) {
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

  private Integer retryQueryPlayerCountForApp(Integer steamAppId) {
    int currentAttempts = 0;
    int backoff = initialBackoff;

    while (currentAttempts < retryLimit) {
      try {
        Map<String, String> args = new HashMap<>();
        args.put("appid", steamAppId.toString());

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
    PlayerCountBatchUpdatedEvent updateCountEvent = new PlayerCountBatchUpdatedEvent(eventArgs);
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

  @Override
  public Duration getExecutionFrequency() {
    return this.executionFrequency;
  }
}
