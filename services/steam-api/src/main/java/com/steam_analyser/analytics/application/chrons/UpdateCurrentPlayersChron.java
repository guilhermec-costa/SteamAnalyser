package com.steam_analyser.analytics.application.chrons;

import org.hibernate.annotations.DialectOverride.OverridesAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.infra.Mediator;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

import static com.steam_analyser.analytics.api.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.SteamRouteMethods.*;

@Component
public class UpdateCurrentPlayersChron implements ISteamChron {

  private SteamConfiguration steamConfiguration;
  private boolean enableToRun = false;

  @Setter
  private LocalDateTime currentExecutionTime;

  @Autowired
  private SteamAppService steamAppService;

  @Autowired
  private SteamAppStatsService steamAppStatsService;

  @Autowired
  private Mediator Mediator;

  public void start(final SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    enableToRun = true;
  }

  @Scheduled(fixedRate = 30000, initialDelay = 150)
  public void run() {
    if (!enableToRun)
      return;

    PlayerCountUpdatedArgument countUpdateEventsArgument;
    currentExecutionTime = getExecutionTime();

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
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
    }

    PlayerCountUpdatedEvent updateCountEvent = new PlayerCountUpdatedEvent(getChronName(), 0, currentExecutionTime);
    Mediator.publish(updateCountEvent); 
  }

  private Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }

  @Override
  public String getChronName() {
    var thisClass = this.getClass();
    return thisClass.getName();
  }
}
