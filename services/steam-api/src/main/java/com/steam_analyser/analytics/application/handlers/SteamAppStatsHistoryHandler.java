package com.steam_analyser.analytics.application.handlers;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.ApplicationEvent;
import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;

@Service
public class SteamAppStatsHistoryHandler implements Handler {

  @Override
  public String getEventName() {
    return PlayerCountUpdatedEvent.class.getName();
  }

  @Override
  public void handle(ApplicationEvent event) {
    System.out.println("evento de atualizar jogador publicado");
  }
  
}
