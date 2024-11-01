package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;

@Service
public class PerfomanceProfiller {
  
  public Instant getNow() {
    return Instant.now();
  }

  public long measureTimeBetween(Instant i1, Instant i2) {
    return Duration.between(i1, i2).toMinutes();
  }
}
