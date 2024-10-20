package com.steam_analyser.analytics.application.events.datatypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class PlayerCountUpdatedArgument {
  
  private final String steamAppId;
  private final int count;
  private final LocalDateTime moment;
}
