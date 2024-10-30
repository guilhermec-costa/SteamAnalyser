package com.steam_analyser.analytics.data.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriorityApp {

  private Integer playerCount;
  private Integer steamAppId;
  private Long localSteamAppId;
}
