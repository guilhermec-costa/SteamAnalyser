package com.steam_analyser.analytics.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.infra.dataAccessors.SteamAppStatsAccessor;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsService {

  private final SteamAppStatsAccessor steamAppStatsAccessor;

  public Optional<SteamAppStatsModel> findAppStatsByAppRegisterId(Long localSteamAppId) {
    return steamAppStatsAccessor.findBySteamAppId(localSteamAppId);
  }

  public SteamAppStatsModel save(SteamAppStatsModel appStat) {
    return steamAppStatsAccessor.save(appStat);
  }

  public SteamAppStatsModel findOrCreateStatsModelInstance(SteamAppModel app) {
    return findAppStatsByAppRegisterId(app.getId())
        .orElse(SteamAppStatsModel.builder().steamApp(app).build());
  }
}
