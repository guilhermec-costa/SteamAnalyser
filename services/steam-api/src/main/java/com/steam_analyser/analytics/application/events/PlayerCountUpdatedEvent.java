package com.steam_analyser.analytics.application.events;

import java.util.List;

import com.steam_analyser.analytics.application.events.datatypes.PartialSteamAppStatsHistory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountUpdatedEvent implements ApplicationEvent {

  @Getter
  private final List<PartialSteamAppStatsHistory> desynchoronizedBatch;

  public String name() {
    return PlayerCountUpdatedEvent.class.getName();
  }
}
