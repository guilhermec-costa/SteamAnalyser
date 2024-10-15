package com.steam_analyser.analytics.api.presentation.SteamResponses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.Data;

public class AppListResponse {

  @JsonProperty("applist")
  private AppsWrapper appsWrapper;
  
  public static class AppsWrapper {

    @JsonProperty("apps")
    private List<App> apps;
  }

  @Data
  public static class App {
    @JsonProperty("appid")
    private Long appId;

    @JsonProperty("name")
    private String name;
  }
}
