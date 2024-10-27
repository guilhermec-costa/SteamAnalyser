package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;

import org.springframework.stereotype.Component;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdatePlayersForPriorityAppsChron implements ISteamChron {

  private final Duration executionFrequency = Duration.ofSeconds(10);
  private final SchedulerManager schedulerManager;
  private SteamConfiguration steamConfiguration;

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
    log.info("Running update UpdatePlayersForPriorityAppsChron");
  }

  @Override
  public void start(SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    schedulerManager.scheduleChronIfAllowed(this);
  }
}
