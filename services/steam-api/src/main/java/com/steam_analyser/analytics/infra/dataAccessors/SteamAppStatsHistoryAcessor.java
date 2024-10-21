package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steam_analyser.analytics.models.SteamAppStatsHistoryModel;

public interface SteamAppStatsHistoryAcessor extends JpaRepository<SteamAppStatsHistoryModel, Long> {
}
