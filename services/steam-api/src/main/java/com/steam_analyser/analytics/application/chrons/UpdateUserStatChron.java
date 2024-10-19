package com.steam_analyser.analytics.application.chrons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.steam.webapi.WebAPI;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import static com.steam_analyser.analytics.api.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.SteamRouteMethods.*;

@Component
public class UpdateUserStatChron {

  private static SteamConfiguration steamConfiguration;
  private static boolean enableToRun = false;

  @Autowired
  private SteamSecretsProperties steamSecrets;

  @Autowired
  private SteamAppService steamAppService;

  @Autowired
  private SteamAppStatsService steamAppStatsService;

  public static void start(final SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    enableToRun = true;
  }

  @Scheduled(fixedRate = 10000)
  private static void syncSteamAppsPlayerCount() {
    if(!enableToRun) return;

    // List<SteamAppModel> tenthApps = steamAppService.getAllSteamApps().subList(31, 41);

    // for (SteamAppModel nextApp : tenthApps) {
    //   String appId = nextApp.getExternalAppId();
    //   SteamAppStatsModel actUponAppStats;

    //   Optional<SteamAppStatsModel> appStatsRegister = steamAppStatsService.findAppStatsByAppId(appId);
    //   if (appStatsRegister.isEmpty())
    //     actUponAppStats = SteamAppStatsModel.builder().steamApp(nextApp).build();
    //   else
    //     actUponAppStats = appStatsRegister.get();

    try {
      Map<String, String> args = new HashMap<>();
      var currentPlayers = steamConfiguration.getWebAPI(ISteamUserStats.string).call(GetNumberOfCurrentPlayers.string, GetNumberOfCurrentPlayers.version);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
      // actUponAppStats.setCurrentPlayers(currentPlayers);
      // actUponAppStats.set_24hpeak(currentPlayers);
      // steamAppStatsService.save(actUponAppStats);
    // }
  }
}
