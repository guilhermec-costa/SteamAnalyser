package com.steam_analyser.analytics.application.schedulers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.externalResponses.SingleSteamApp;
import com.steam_analyser.analytics.application.services.CloudFlareService;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.data.models.SteamAppModel;

import java.time.Duration;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import java.util.concurrent.Executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.*;

@Component
@Slf4j
public class RegisterNewAppsChron implements ISteamChron {

  private SteamAppService steamAppService;
  private SteamConfiguration steamConfiguration;
  private ProfillingService profillingService;
  private Executor executor;
  private CloudFlareService cloudFlareService;
  private SchedulerManager schedulerManager;

  private final Duration executionFrequency = Duration.ofMinutes(90);

  public RegisterNewAppsChron(
      SteamAppService steamAppService,
      ProfillingService profillingService,
      CloudFlareService cloudFlareService,
      SchedulerManager schedulerManager) {
    this.steamAppService = steamAppService;
    this.profillingService = profillingService;
    this.executor = Executors.newFixedThreadPool(16);
    this.cloudFlareService = cloudFlareService;
    this.schedulerManager = schedulerManager;
  }

  @Override
  public void start(final SteamConfiguration steamConfiguration) {
    this.steamConfiguration = steamConfiguration;
    schedulerManager.scheduleChronIfAllowed(this);
  }

  @Override
  public void run() {
    var parsedApps = fetchAllSteamApps();
    if (parsedApps.isEmpty())
      return;

    var start = profillingService.getNow();
    List<CompletableFuture<Void>> futures = parsedApps.stream().map(this::createOrPassAsync)
        .collect(Collectors.toList());
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    var end = profillingService.getNow();
    var executionDuration = profillingService.measureTimeBetween(start, end);
    log.info("Finishing execution of \"" + getChronName() + "\" in " + executionDuration);
  }

  private List<SingleSteamApp> fetchAllSteamApps() {
    log.info("Fetching steam apps fron Steam Web API");
    try {
      var existingsAppsResponse = steamConfiguration.getWebAPI(ISteamApps.string)
          .call("GET", GetAppList.string, GetAppList.version);
      return parseSteamAppsResponse(existingsAppsResponse);
    } catch (Exception e) {
      return List.of();
    }
  }

  private CompletableFuture<Void> createOrPassAsync(final SingleSteamApp app) {
    return CompletableFuture.runAsync(() -> {
      final String appHeaderUrl = cloudFlareService.buildAppHeaderUrl(app.getAppId());
      Optional<SteamAppModel> storedApp = steamAppService.findAppBySteamAppId(app.getAppId());
      if (storedApp.isEmpty()) {
        SteamAppModel newApp = new SteamAppModel(app.getName(), app.getAppId(), appHeaderUrl);
        steamAppService.saveOne(newApp);
      }
    }, executor);
  }

  private List<SingleSteamApp> parseSteamAppsResponse(KeyValue response) {
    List<SingleSteamApp> parsedApps = new ArrayList<>();
    var appsContainer = response.getChildren().get(0).getChildren();
    for (var app : appsContainer) {
      var appContent = app.getChildren();
      var appId = appContent.get(0).getValue();
      var appName = appContent.get(1).getValue();
      parsedApps.add(new SingleSteamApp(Integer.parseInt(appId), appName));
    }
    return parsedApps;
  }

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  @Override
  public Duration getExecutionFrequency() {
    return this.executionFrequency;
  }
}
