package com.steam_analyser.analytics.api.presentation.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamAppResponse {

  @JsonProperty("steam_app_id")
  private String steamAppId;

  @JsonProperty("name")
  private String name;

  @JsonProperty("appHeader")
  private String appImage;
}
