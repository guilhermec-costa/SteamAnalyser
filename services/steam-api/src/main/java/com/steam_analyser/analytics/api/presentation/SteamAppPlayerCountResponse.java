package com.steam_analyser.analytics.api.presentation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamAppPlayerCountResponse {

  @JsonProperty("response")
  private ResponseWrapper responseWrapper;
  
  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ResponseWrapper {

    @JsonProperty(value = "player_count")
    private int playerCount;
  }

  public int getPlayerCount() {
    return responseWrapper.playerCount;
  }
}
