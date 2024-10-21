package com.steam_analyser.analytics.application.chrons;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;

@Component
public class Update24hPeakChron implements ISteamChron {

  private SteamConfiguration steamConfiguration;
  private boolean enableToRun = false;

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  @Override
  @Scheduled(fixedRate = 2000)
  public void run() {
    if (!enableToRun)
      return;

    System.out.println("Running update 25 hours peak");
  }

  @Override
  public void start(SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    enableToRun = true;
  }
  
}
