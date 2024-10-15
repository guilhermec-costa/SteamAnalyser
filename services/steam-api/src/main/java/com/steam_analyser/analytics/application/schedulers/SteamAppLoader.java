package com.steam_analyser.analytics.application.schedulers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse.App;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.domain.entities.SteamApp;
import com.steam_analyser.analytics.domain.repositoryInterfaces.SteamAppRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SteamAppLoader {
  
  private final SteamAppService steamAppService;
  private final ModelMappingService modelMappingService;
  private final SteamAppRepository steamAppRepository;

  @Transactional
  public void synchronizeExistingApps() throws Exception {
    var appsResponse = steamAppService.getAllApps();
    List<App> apps = appsResponse.getAppsWrapper().getApps();
    List<SteamApp> parsedAppsToSteamApps = modelMappingService.mapList(apps, SteamApp.class);
    steamAppRepository.saveAll(parsedAppsToSteamApps);
  }
}
