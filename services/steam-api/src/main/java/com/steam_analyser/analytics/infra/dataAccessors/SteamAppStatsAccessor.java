package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steam_analyser.analytics.models.SteamAppStatsModel;

public interface SteamAppStatsAccessor extends JpaRepository<SteamAppStatsModel, Long> {
}
