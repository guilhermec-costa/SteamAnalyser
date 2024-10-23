package com.steam_analyser.analytics.application.schedulers;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import java.time.LocalDateTime;

public interface ISteamChron {
  
  void start(final SteamConfiguration theSteamConfiguration);
  String getChronName();
  default LocalDateTime getExecutionDate() {
    return LocalDateTime.now();
  };
  void run();
}
