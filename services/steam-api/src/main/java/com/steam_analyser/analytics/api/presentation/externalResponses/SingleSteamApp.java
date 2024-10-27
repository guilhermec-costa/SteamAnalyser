package com.steam_analyser.analytics.api.presentation.externalResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleSteamApp {

  private Integer appId;
  private String name;

}
