package com.steam_analyser.analytics.application.schedulers;

import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.api.presentation.externalResponses.SingleSteamApp;
import com.steam_analyser.analytics.application.services.CloudFlareService;
import com.steam_analyser.analytics.application.services.ProfillingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamWebAPIService;
import com.steam_analyser.analytics.data.models.SteamAppModel;

import java.time.Duration;

import java.util.concurrent.Executor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class RegisterNewAppsChron implements ISteamChron {

  private final SteamAppService steamAppService;
  private final ProfillingService profillingService;
  private final Executor executor = Executors.newFixedThreadPool(16);
  private final CloudFlareService cloudFlareService;
  private final SteamWebAPIService steamWebAPIService;
  private final SchedulerManager schedulerManager;
  private final Duration executionFrequency = Duration.ofMinutes(90);

  @Override
  public void start() {
    schedulerManager.scheduleChronIfAllowed(this);
  }

  @Override
  public void run() {
    var parsedApps = steamWebAPIService.fetchAllSteamApps();
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

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  @Override
  public Duration getExecutionFrequency() {
    return this.executionFrequency;
  }
}
