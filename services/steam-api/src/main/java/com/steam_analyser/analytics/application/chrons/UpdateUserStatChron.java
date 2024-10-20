package com.steam_analyser.analytics.application.chrons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import static com.steam_analyser.analytics.api.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.SteamRouteMethods.*;

@Component
public class UpdateUserStatChron implements ISteamChron {

  private SteamConfiguration steamConfiguration;
  private boolean enableToRun = false;

  @Autowired
  private SteamAppService steamAppService;

  @Autowired
  private SteamAppStatsService steamAppStatsService;

  public void makeSchedulable(final SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    enableToRun = true;
  }

  @Scheduled(fixedRate = 30000, initialDelay = 500)
  public void run() {
    if (!enableToRun)
      return;

    System.out.println("Running updateUserStatsChron job");
    List<SteamAppModel> localSteamApps = steamAppService.findNSteamApps(PageRequest.of(0, 500));
    for (SteamAppModel nextApp : localSteamApps) {
      Long localAppId = nextApp.getId();
      String steamAppId = nextApp.getSteamAppId();
      SteamAppStatsModel actUponAppStats;

      Optional<SteamAppStatsModel> localAppStatsRegister = steamAppStatsService.findAppStatsByAppRegisterId(localAppId);
      if (localAppStatsRegister.isEmpty())
        actUponAppStats = SteamAppStatsModel.builder().steamApp(nextApp).build();
      else
        actUponAppStats = localAppStatsRegister.get();

      try {
        Map<String, String> args = new HashMap<>();
        args.put("appid", steamAppId);

        var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string).call(
            GetNumberOfCurrentPlayers.string,
            GetNumberOfCurrentPlayers.version, args);

        Integer playerCount = extractPlayerCountingFromResponse(currentPlayersResponse);
        actUponAppStats.setCurrentPlayers(playerCount);

        steamAppStatsService.save(actUponAppStats);
        System.out.println(playerCount);
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }
  }

  private Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }
}
