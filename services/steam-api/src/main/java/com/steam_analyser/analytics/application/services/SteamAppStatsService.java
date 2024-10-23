package com.steam_analyser.analytics.application.services;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.infra.dataAccessors.SteamAppStatsAccessor;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsService {

  private final SteamAppStatsAccessor steamAppStatsAccessor;
  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final SteamAppService steamAppService;

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

  public void saveMultiple(List<SteamAppStatsModel> list) {
    steamAppStatsAccessor.saveAll(list);
  }

  public void updateApp24Peak(Long localAppId) {
    var app = findAppStatsByAppRegisterId(localAppId);
    if (app.isPresent()) {
      var appInstance = app.get();
      Integer _24peak = steamAppStatsHistoryService.queryApp24peak(localAppId);
      appInstance.set_24hpeak(_24peak);
      save(appInstance);
    }
  }

  public Integer currentPlayersForApp(final Long localSteamAppId) {
    SteamAppStatsModel app = steamAppStatsAccessor.findBySteamAppId(localSteamAppId)
      .orElseThrow(() -> new IllegalArgumentException("App does not exist"));

    return app.getCurrentPlayers();
  }
}
