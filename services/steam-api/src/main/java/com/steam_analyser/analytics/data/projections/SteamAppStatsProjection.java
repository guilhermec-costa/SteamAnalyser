package com.steam_analyser.analytics.data.projections;

import java.time.LocalDateTime;

public interface SteamAppStatsProjection {
    LocalDateTime getUpdatedAt();
    Integer get_24hpeak();
    Integer getCurrentPlayers();
    String getName();
}
