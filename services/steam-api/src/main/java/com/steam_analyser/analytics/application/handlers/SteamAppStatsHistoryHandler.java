package com.steam_analyser.analytics.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.services.SteamAppStatsHistoryService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsHistoryHandler implements Handler<PlayerCountUpdatedEvent> {

  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final SteamAppStatsService steamAppStatsService;
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public String getEventName() {
    return PlayerCountUpdatedEvent.class.getName();
  }

  @Override
  public void handle(PlayerCountUpdatedEvent event) {
    var historyInstances = steamAppStatsHistoryService.mountListFromPartials(event.getDesynchoronizedBatch());
    for (var history : historyInstances) {
      steamAppStatsService.updateApp24Peak(history.getSteamApp().getId());
    }
    steamAppStatsHistoryService.saveMultiple(historyInstances);
    logger.info("PlayerCountUpdatedEvent computed!");
  }

}
