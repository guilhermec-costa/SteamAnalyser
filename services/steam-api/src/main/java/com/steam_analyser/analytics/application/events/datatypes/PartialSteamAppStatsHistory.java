package com.steam_analyser.analytics.application.events.datatypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.steam_analyser.analytics.models.SteamAppModel;

@RequiredArgsConstructor
@Getter
@Setter
public class PartialSteamAppStatsHistory {
  
  private final SteamAppModel steamApp;
  private final Integer count;
  private final LocalDateTime snapshoted_at;
}
