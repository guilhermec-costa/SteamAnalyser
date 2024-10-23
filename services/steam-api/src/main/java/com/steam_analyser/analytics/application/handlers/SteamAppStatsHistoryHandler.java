package com.steam_analyser.analytics.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;
import com.steam_analyser.analytics.infra.dataAccessors.SteamAppStatsHistoryAcessor;
import com.steam_analyser.analytics.models.SteamAppStatsHistoryModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsHistoryHandler implements Handler<PlayerCountUpdatedEvent> {

  private final SteamAppStatsHistoryAcessor steamAppStatsHistoryAcessor;
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String getEventName() {
    return PlayerCountUpdatedEvent.class.getName();
  }

  @Override
  public void handle(PlayerCountUpdatedEvent event) {
    // logger.info("Modifying History");
    var args = event.getArgs();
    for (PlayerCountUpdatedArgument arg : args) {
      SteamAppStatsHistoryModel historyInstance = new SteamAppStatsHistoryModel(
          arg.getSteamApp(),
          arg.getCount(),
          arg.getSnapshoted_at());

      steamAppStatsHistoryAcessor.save(historyInstance);
    }

    logger.info("History has been modified");
  }

}
