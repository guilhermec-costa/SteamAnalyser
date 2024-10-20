package com.steam_analyser.analytics.application.chrons;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import java.time.LocalDateTime;

public interface ISteamChron {
  
  void start(final SteamConfiguration theSteamConfiguration);
  String getChronName();
  default LocalDateTime getExecutionTime() {
    return LocalDateTime.now();
  };
  void run();
}
