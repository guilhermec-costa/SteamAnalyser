package com.steam_analyser.analytics.data.projections;

import java.time.LocalDateTime;

public interface SteamAppStatsProjection {
    LocalDateTime getUpdatedAt();
    int get_24hpeak();
    int getCurrentPlayers();
    String getName();
}
