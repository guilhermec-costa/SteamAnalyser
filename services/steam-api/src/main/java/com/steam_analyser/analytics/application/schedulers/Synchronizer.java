package com.steam_analyser.analytics.application.schedulers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse.App;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.UserStatsService;
import com.steam_analyser.analytics.application.services.WebSteamAPIClientService;
import com.steam_analyser.analytics.domain.entities.SteamApp;
import com.steam_analyser.analytics.domain.entities.SteamAppStats;
import com.steam_analyser.analytics.domain.repositoryInterfaces.SteamAppRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Synchronizer {

  private final SteamAppService steamAppService;
  private final UserStatsService userStatsService;
  private final ModelMappingService modelMappingService;
  private final WebSteamAPIClientService webSteamAPIClientService;

  @Transactional
  public void synchronizeExistingApps() throws Exception {
    var appsResponse = webSteamAPIClientService.getAllApps();
    List<App> apps = appsResponse.getAppsWrapper().getApps();
    List<SteamApp> parsedAppsToSteamApps = modelMappingService.mapList(apps, SteamApp.class);
    steamAppService.saveApps(parsedAppsToSteamApps);
  }

  public void synchronizeAppStats() throws Exception {
    var registeredApps = steamAppService.getAllSteamApps();
    var tenFirst = registeredApps.subList(31, 41);
    for (var app : tenFirst) {
      Optional<SteamAppStats> appStatsRegister = userStatsService.findSteamAppStatsById(app.getExternalAppId());
      if (appStatsRegister.isPresent()) {
        Integer currentPlayers = webSteamAPIClientService.getNumberOfCurrentPlayersForApp(app.getExternalAppId());
        var appStats = appStatsRegister.get();
        appStats.setCurrentPlayers(currentPlayers);
        appStats.set_24hpeak(currentPlayers);
        continue;
      }
    }
  }
}
