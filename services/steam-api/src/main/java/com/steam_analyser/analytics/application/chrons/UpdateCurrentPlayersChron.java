package com.steam_analyser.analytics.application.chrons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

import static com.steam_analyser.analytics.api.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.SteamRouteMethods.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdateCurrentPlayersChron implements ISteamChron {

  private final SteamAppService steamAppService;
  private final SteamAppStatsService steamAppStatsService;
  private final Mediator mediator;
  private SteamConfiguration steamConfiguration;
  private boolean enableToRun = false;
  private final int executionFrequency = 30000;

  @Setter
  private LocalDateTime currentExecutionTime;

  public void start(final SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    enableToRun = true;
    log.info("Executing task: \"" + getChronName() + "\"");
  }

  @Override
  @Scheduled(fixedRate = executionFrequency)
  public void run() {
    if (!enableToRun)
      return;

    List<PlayerCountUpdatedArgument> toBePropagated = new ArrayList<>();
    currentExecutionTime = getExecutionTime();

    List<SteamAppModel> localSteamApps = steamAppService.findNSteamApps(PageRequest.of(0, 100));
    for (SteamAppModel nextApp : localSteamApps) {
      SteamAppStatsModel actUponAppStats = steamAppStatsService.findOrCreateStatsModelInstance(nextApp);
      Integer playerCount = queryPlayerCountForApp(nextApp.getSteamAppId());
      actUponAppStats.updateCurrentPlayers(playerCount);
      steamAppStatsService.save(actUponAppStats);

      toBePropagated.add(new PlayerCountUpdatedArgument(nextApp, playerCount, LocalDateTime.now()));
    }

    propagateSideEffects(toBePropagated);
  }

  private Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  private Integer queryPlayerCountForApp(String steamAppId) {
    try {
      Map<String, String> args = new HashMap<>();
      args.put("appid", steamAppId);

      var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string)
          .call(
              GetNumberOfCurrentPlayers.string,
              GetNumberOfCurrentPlayers.version, args);

      return extractPlayerCountingFromResponse(currentPlayersResponse);
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  private void propagateSideEffects(List<PlayerCountUpdatedArgument> eventArgs) {
    PlayerCountUpdatedEvent updateCountEvent = new PlayerCountUpdatedEvent(eventArgs);
    mediator.publish(updateCountEvent);
  }
}
