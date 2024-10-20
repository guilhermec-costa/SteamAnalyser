package com.steam_analyser.analytics.application.events;

import java.util.List;

import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountUpdatedEvent implements ApplicationEvent {

  @Getter
  private final List<PlayerCountUpdatedArgument> args;

  public String name() {
    return PlayerCountUpdatedEvent.class.getName();
  }
}
