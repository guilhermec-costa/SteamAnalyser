package com.steam_analyser.analytics.application.events;

import java.util.List;

import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountBatchUpdatedEvent implements ApplicationEvent {

  @Getter
  private final List<PartialSteamAppStatsHistory> desynchoronizedBatch;

  public String name() {
    return PlayerCountBatchUpdatedEvent.class.getName();
  }
}
