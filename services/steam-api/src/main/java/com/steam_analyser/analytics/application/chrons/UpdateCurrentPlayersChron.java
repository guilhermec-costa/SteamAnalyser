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
import java.util.ArrayList;
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

  @Scheduled(fixedRate = 10000, initialDelay = 150)
  public void run() {
    if (!enableToRun)
      return;

    List<PlayerCountUpdatedArgument> eventsArguments = new ArrayList<>();
    currentExecutionTime = getExecutionTime();

    List<SteamAppModel> localSteamApps = steamAppService.findNSteamApps(PageRequest.of(0, 5));
    for (SteamAppModel nextApp : localSteamApps) {
      SteamAppStatsModel actUponAppStats = findOrCreateStatsModelInstance(nextApp);
      Integer playerCount = queryPlayerCountForApp(nextApp.getSteamAppId());
      eventsArguments.add(new PlayerCountUpdatedArgument(nextApp.getSteamAppId(), playerCount, LocalDateTime.now()));
      steamAppStatsService.save(actUponAppStats);
    }

    propateSideEffects(eventsArguments);
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

  private SteamAppStatsModel findOrCreateStatsModelInstance(SteamAppModel app) {
    return steamAppStatsService.findAppStatsByAppRegisterId(app.getId())
        .orElse(SteamAppStatsModel.builder().steamApp(app).build());
  }

  private Integer queryPlayerCountForApp(String steamAppId) {
    try {
      Map<String, String> args = new HashMap<>();
      args.put("appid", steamAppId);

      var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string).call(
          GetNumberOfCurrentPlayers.string,
          GetNumberOfCurrentPlayers.version, args);

      return extractPlayerCountingFromResponse(currentPlayersResponse);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  private void propateSideEffects(List<PlayerCountUpdatedArgument> eventArgs) {
    PlayerCountUpdatedEvent updateCountEvent = new PlayerCountUpdatedEvent(eventArgs);
    Mediator.publish(updateCountEvent);
  }
}
