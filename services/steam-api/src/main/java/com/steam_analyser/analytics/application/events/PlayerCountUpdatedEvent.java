package com.steam_analyser.analytics.application.events;

import java.util.List;

import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountUpdatedEvent implements ApplicationEvent {

  private List<PlayerCountUpdatedArgument> updateParam;

  public String name() {
    return this.getClass().getName();
  }
}
