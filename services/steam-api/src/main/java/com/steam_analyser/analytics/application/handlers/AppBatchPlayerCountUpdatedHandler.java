package com.steam_analyser.analytics.application.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.PlayerCountBatchUpdatedEvent;
import com.steam_analyser.analytics.application.services.SteamAppStatsHistoryService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AppBatchPlayerCountUpdatedHandler implements Handler<PlayerCountBatchUpdatedEvent> {

  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final SteamAppStatsService steamAppStatsService;

  @Override
  public String getEventName() {
    return PlayerCountBatchUpdatedEvent.class.getName();
  }

  @Override
  public void handle(PlayerCountBatchUpdatedEvent event) {
    var historyInstances = steamAppStatsHistoryService.mountListFromPartials(event.getDesynchoronizedBatch());
    for (var history : historyInstances) {
      steamAppStatsService.updateApp24Peak(history.getSteamApp().getId());
    }
    steamAppStatsHistoryService.saveMultiple(historyInstances);
    log.info("PlayerCountUpdatedEvent computed!");
  }

}
