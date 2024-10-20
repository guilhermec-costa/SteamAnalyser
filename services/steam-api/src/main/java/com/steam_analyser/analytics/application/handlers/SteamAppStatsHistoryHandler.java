package com.steam_analyser.analytics.application.handlers;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;

@Service
public class SteamAppStatsHistoryHandler implements Handler<PlayerCountUpdatedEvent> {

  @Override
  public String getEventName() {
    return PlayerCountUpdatedEvent.class.getName();
  }

  @Override
  public void handle(PlayerCountUpdatedEvent event) {
    var args = event.getArgs();
    for(PlayerCountUpdatedArgument arg : args) {
      System.out.println("side effects running");
    }
  }
   
}
