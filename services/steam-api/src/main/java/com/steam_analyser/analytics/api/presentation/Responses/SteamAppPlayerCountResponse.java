package com.steam_analyser.analytics.api.presentation.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamAppPlayerCountResponse {

  @JsonProperty("player_count")
  private Integer playerCount;
}
