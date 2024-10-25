package com.steam_analyser.analytics.api.presentation.externalResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleSteamApp {

  private String appId;
  private String name;

}
