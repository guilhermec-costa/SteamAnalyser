package com.steam_analyser.analytics.data.projections;

public interface PriorityAppsProjection {
  Integer getCurrentPlayers();

  Long getId();

  Integer getSteamAppId();
}
