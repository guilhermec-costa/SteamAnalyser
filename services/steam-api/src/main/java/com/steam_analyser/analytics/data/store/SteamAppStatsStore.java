package com.steam_analyser.analytics.data.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.data.models.SteamAppStatsModel;

import java.util.Optional;


@Repository
public interface SteamAppStatsStore extends JpaRepository<SteamAppStatsModel, Long> {

  Optional<SteamAppStatsModel> findBySteamAppId(Long localSteamAppId);
}
