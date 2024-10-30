package com.steam_analyser.analytics.data.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.steam_analyser.analytics.data.models.SteamAppStatsHistoryModel;
import com.steam_analyser.analytics.data.store.custom.CustomSteamAppStatsHistoryStore;

import io.lettuce.core.dynamic.annotation.Param;

public interface SteamAppStatsHistoryStore
    extends JpaRepository<SteamAppStatsHistoryModel, Long>, CustomSteamAppStatsHistoryStore {
  @Query(value = """
      select max(player_count) from steam_app_stats_history
      where local_steam_app_id = :localSteamAppId and
      snapshoted_at between now() - interval '24 hours' and now()
      """, nativeQuery = true)
  Integer queryApp24hPeak(@Param("localSteamAppId") Long localSteamAppId);
}
