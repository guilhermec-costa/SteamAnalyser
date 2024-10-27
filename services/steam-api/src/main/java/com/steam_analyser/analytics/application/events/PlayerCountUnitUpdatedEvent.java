package com.steam_analyser.analytics.application.events;

import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountUnitUpdatedEvent implements ApplicationEvent {

  @Getter
  private final PartialSteamAppStatsHistory appHistoryArg;

  public String name() {
    return PlayerCountUnitUpdatedEvent.class.getName();
  }
}
