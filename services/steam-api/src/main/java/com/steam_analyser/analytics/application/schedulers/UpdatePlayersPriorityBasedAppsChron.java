package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountUnitUpdatedEvent;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.application.services.SteamWebAPIProcessor;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;
import com.steam_analyser.analytics.data.types.PriorityApp;
import com.steam_analyser.analytics.infra.mediator.Mediator;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdatePlayersPriorityBasedAppsChron implements ISteamChron {

  private final Duration executionFrequency = Duration.ofMinutes(15);
  private final SchedulerManager schedulerManager;
  private final Mediator mediator;
  private final SteamAppStatsService steamAppStatsService;
  private final ProfillingService profillingService;
  private final SteamAppService steamAppService;
  private final Executor executor = Executors.newFixedThreadPool(12);
  private final SteamWebAPIProcessor steamWebAPIService;
  private final int MAX_APPS_QUERY_LIMIT = 300000;
  private final int PRIMARY_PRIORITY_OFFSET = 7500;
  private final int SECONDARY_PRIORITY_OFFSET = PRIMARY_PRIORITY_OFFSET + 1;

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  @Override
  public Duration getExecutionFrequency() {
    return this.executionFrequency;
  }

  @Override
  public void run() {
    var start = profillingService.getNow();
    var primaryPriorityApps = steamAppStatsService.queryByPlayersPriorityOffset(0, PRIMARY_PRIORITY_OFFSET);
    var secondaryPriorityApps = steamAppStatsService.queryByPlayersPriorityOffset(SECONDARY_PRIORITY_OFFSET, MAX_APPS_QUERY_LIMIT);
    List<CompletableFuture<Void>> primaryFutures = primaryPriorityApps.stream().map(this::processExistingAppStats)
        .collect(Collectors.toList());

    List<CompletableFuture<Void>> secondaryFutures = secondaryPriorityApps.stream().map(this::processExistingAppStats)
        .collect(Collectors.toList());

    CompletableFuture.allOf(primaryFutures.toArray(new CompletableFuture[0])).join();
    CompletableFuture.allOf(secondaryFutures.toArray(new CompletableFuture[0])).join();

    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  private CompletableFuture<Void> processExistingAppStats(PriorityApp projection) {
    return CompletableFuture.runAsync(() -> {
      Integer newPlayerCount = steamWebAPIService.retryQueryPlayerCountForApp(projection.getSteamAppId());
      var actUponAppStats = steamAppStatsService.findAppStatsByAppRegisterId(projection.getLocalSteamAppId());

      if (steamAppStatsService.canUpdateAppStatsPlayerCount(projection.getPlayerCount(), newPlayerCount)) {
        if (actUponAppStats.isPresent()) {
          var stat = actUponAppStats.get();
          stat.updateCurrentPlayers(newPlayerCount);
          steamAppStatsService.saveOne(stat);

          var steamApp = steamAppService.findAppById(projection.getLocalSteamAppId());
          var sideEffectArg = new PartialSteamAppStatsHistory(steamApp, newPlayerCount, getExecutionDate());
          mediator.publish(new PlayerCountUnitUpdatedEvent(sideEffectArg));
        }
      }
    }, executor);
  }

  @Override
  public void start() {
    schedulerManager.scheduleChronIfAllowed(this);
  }
}
