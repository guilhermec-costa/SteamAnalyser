package com.steam_analyser.analytics.application.handlers;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.PlayerCountUnitUpdatedEvent;
import com.steam_analyser.analytics.application.services.SteamAppStatsHistoryService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppUnitPlayerCountUpdatedHandler implements Handler<PlayerCountUnitUpdatedEvent> {

  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final SteamAppStatsService steamAppStatsService;

  @Override
  public String getEventName() {
    return PlayerCountUnitUpdatedEvent.class.getName();
  }

  @Override
  public void handle(PlayerCountUnitUpdatedEvent event) {
    var historyInstance = steamAppStatsHistoryService.mountFromPartial(event.getAppHistoryArg());
    steamAppStatsHistoryService.saveOne(historyInstance);
    steamAppStatsService.updateApp24Peak(historyInstance.getSteamApp().getId());
  }

}
