package com.steam_analyser.analytics.api.presentation;

import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AppDetailsResponse {
  
  private boolean success;

  private String varyName;

  @JsonProperty("data")
  private GameData gameData;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class GameData {
    private String type;
    private String name;
    private int steamAppid;
    private int requiredAge;
    private boolean isFree;
    private List<Integer> dlc;
  }
}
