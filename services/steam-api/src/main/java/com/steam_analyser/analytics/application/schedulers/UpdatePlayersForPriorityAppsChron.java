package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdatePlayersForPriorityAppsChron implements ISteamChron {

  private final Duration executionFrequency = Duration.ofMinutes(15);
  private final SchedulerManager schedulerManager;
  private final SteamAppStatsService steamAppStatsService;

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
    var priorityApps = steamAppStatsService.queryByPlayersPriority(PageRequest.of(0, 7500));
    log.info("Running update UpdatePlayersForPriorityAppsChron");
  }

  @Override
  public void start() {
    schedulerManager.scheduleChronIfAllowed(this);
  }
}
