package com.steam_analyser.analytics.application.schedulers;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.externalResponses.SingleSteamApp;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;

import java.time.Duration;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class RegisterNewAppsChron implements ISteamChron {

  private final TaskScheduler taskScheduler;
  private final SteamAppService steamAppService;
  private SteamConfiguration steamConfiguration;

  @Override
  @SuppressWarnings("deprecation")
  public void start(final SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    taskScheduler.scheduleAtFixedRate(this::run, executionFrequency);
    log.info("Executing task: \"" + getChronName() + "\"");
  }

  private final Duration executionFrequency = Duration.ofMinutes(90);

  @Override
  public void run() {
    var parsedApps = queryAllSteamApps();
    if (parsedApps.isEmpty())
      return;

    for (var app : parsedApps) {
      Optional<SteamAppModel> storedApp = steamAppService.findAppBySteamAppId(app.getAppId());
      if (storedApp.isPresent())
        continue;
      SteamAppModel newApp = new SteamAppModel(app.getName(), app.getAppId());
      steamAppService.saveOne(newApp);
    }
    log.info("Finishing execution of \"" + getChronName() + "\"");
  }

  private List<SingleSteamApp> queryAllSteamApps() {
    try {
      var existingsAppsResponse = steamConfiguration.getWebAPI(ISteamApps.string)
          .call("GET", GetAppList.string, GetAppList.version);
      return parseSteamAppsResponse(existingsAppsResponse);
    } catch (Exception e) {
      return List.of();
    }
  }

  private List<SingleSteamApp> parseSteamAppsResponse(KeyValue response) {
    List<SingleSteamApp> parsedApps = new ArrayList<>();
    var appsContainer = response.getChildren().get(0).getChildren();
    for (var app : appsContainer) {
      var appContent = app.getChildren();
      var appId = appContent.get(0).getValue();
      var appName = appContent.get(1).getValue();
      parsedApps.add(new SingleSteamApp(appName, appId));
    }
    return parsedApps;
  }

  @Override
  public String getChronName() {
    return getClass().getName();
  }
}
