package com.steam_analyser.analytics.application.schedulers;

import java.time.Duration;
import java.time.LocalDateTime;

public interface ISteamChron {

  void start();

  String getChronName();

  Duration getExecutionFrequency();

  default LocalDateTime getExecutionDate() {
    return LocalDateTime.now();
  };

  void run();
}
