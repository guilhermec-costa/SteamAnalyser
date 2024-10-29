package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountUnitUpdatedEvent;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.application.services.SteamWebAPIService;
import com.steam_analyser.analytics.data.projections.PriorityAppsProjection;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;
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
  private final SteamWebAPIService steamWebAPIService;

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
    var priorityApps = steamAppStatsService.queryByPlayersPriority(PageRequest.of(0, 7500));
    List<CompletableFuture<Void>> futures = priorityApps.stream().map(this::processExistingAppStats)
        .collect(Collectors.toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  private CompletableFuture<Void> processExistingAppStats(PriorityAppsProjection projection) {
    return CompletableFuture.runAsync(() -> {
      Integer newPlayerCount = steamWebAPIService.retryQueryPlayerCountForApp(projection.getSteamAppId());
      var actUponAppStats = steamAppStatsService.findAppStatsByAppRegisterId(projection.getId());

      if (steamAppStatsService.canUpdateAppStatsPlayerCount(projection.getCurrentPlayers(), newPlayerCount)) {
        if (actUponAppStats.isPresent()) {
          var stat = actUponAppStats.get();
          stat.updateCurrentPlayers(newPlayerCount);
          steamAppStatsService.saveOne(stat);

          var steamApp = steamAppService.findAppById(projection.getId());
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
