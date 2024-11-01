package com.steam_analyser.analytics.data.projections;

import java.time.LocalDateTime;

public interface AppHistoryProjection {

  Integer getPlayerCount();
  LocalDateTime getSnapshotedAt();
}
