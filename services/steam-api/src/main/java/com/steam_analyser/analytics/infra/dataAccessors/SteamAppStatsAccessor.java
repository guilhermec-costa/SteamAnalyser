package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.steam_analyser.analytics.models.SteamAppStatsModel;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SteamAppStatsAccessor extends JpaRepository<SteamAppStatsModel, Long> {

  Optional<SteamAppStatsModel> findBySteamAppId(Long localSteamAppId);
}
