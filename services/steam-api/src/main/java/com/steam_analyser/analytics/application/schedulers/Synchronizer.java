package com.steam_analyser.analytics.application.schedulers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse.App;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
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
  private final SteamAppStatsService steamAppStatsService;
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
    List<SteamApp> tenthApps = steamAppService.getAllSteamApps().subList(31, 41);

    for (SteamApp nextApp : tenthApps) {
      String appId = nextApp.getExternalAppId();
      SteamAppStats actUponAppStats;

      Optional<SteamAppStats> appStatsRegister = steamAppStatsService.findAppStatsByAppId(appId);
      if (appStatsRegister.isEmpty()) 
        actUponAppStats = SteamAppStats.builder().steamApp(nextApp).build();
      else actUponAppStats = appStatsRegister.get();

      Integer currentPlayers = webSteamAPIClientService.getNumberOfCurrentPlayersForApp(appId);
      actUponAppStats.setCurrentPlayers(currentPlayers);
      actUponAppStats.set_24hpeak(currentPlayers);
      steamAppStatsService.save(actUponAppStats);
    }
  }
}
