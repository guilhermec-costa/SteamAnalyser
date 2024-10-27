package com.steam_analyser.analytics.data.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.projections.SteamAppStatsProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface SteamAppStatsStore extends JpaRepository<SteamAppStatsModel, Long> {

  Optional<SteamAppStatsModel> findBySteamAppId(Long localSteamAppId);

  @Query(value = """
      select sacs.updated_at, sacs._24hpeak, sacs.current_players, sa."name", sa.app_image
      from steam_app_current_stats sacs
      inner join steam_app sa on sa.id = sacs.local_steam_app_id
      where sacs.current_players > 0
      """, nativeQuery = true)
  Page<SteamAppStatsProjection> presentAppsStatsQuery(Pageable pageable);

  @Query(value = """
      select sacs.current_players, sa.steam_app_id from steam_app_current_stats sacs
      inner join steam_app sa on sa.id = sacs.local_steam_app_id 
      order by sacs.current_players desc
      """, nativeQuery = true)
  List<SteamAppStatsProjection> findAllByCurrentPlayersPriority(Pageable pageable);
}
