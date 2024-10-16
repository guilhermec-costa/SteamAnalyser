package com.steam_analyser.analytics.domain.repositoryInterfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.steam_analyser.analytics.domain.entities.SteamAppStats;

public interface SteamAppStatsRepository extends JpaRepository<SteamAppStats, Long> {
}
