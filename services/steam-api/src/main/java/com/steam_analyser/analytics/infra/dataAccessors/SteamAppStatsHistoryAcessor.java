package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.steam_analyser.analytics.models.SteamAppStatsHistoryModel;

import io.lettuce.core.dynamic.annotation.Param;

public interface SteamAppStatsHistoryAcessor extends JpaRepository<SteamAppStatsHistoryModel, Long> {
  @Query(value = """
      select max(player_count) from steam_app_stats_history
      where local_steam_app_id = :localSteamAppId and
      snapshoted_at between now() - interval '24 hours' and now()
      """, nativeQuery = true)
  Integer queryApp24hPeak(@Param("localSteamAppId") Long localSteamAppId);
}
