package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.services.PerfomanceProfiller;
import com.steam_analyser.analytics.application.services.SteamAppStatsHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class PurgeHistoryChron implements ISteamChron {

  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final PerfomanceProfiller profillingService;
  private final SchedulerManager schedulerManager;

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  @Override
  public Duration getExecutionFrequency() {
    return Duration.ofSeconds(5);
  }

  @Override
  public void run() {
    var start = profillingService.getNow();

    CompletableFuture.runAsync(() -> {
      steamAppStatsHistoryService.purgeExpiredHistories();
    }).join();

    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  @Override
  public void start() {
    schedulerManager.scheduleChronIfAllowed(this);
  }

}
