package com.steam_analyser.analytics.api.presentation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SteamMostPlayedGamesResponse {
  private Response response;

  @Data
  public static class Response {
    private long rollup_date;
    private List<Rank> ranks;
  }

  @Data
  public static class Rank {
    private int rank;
    private int appid;

    private int last_week_rank;

    @JsonProperty("peak_in_game")
    private int peakInGame;

    public String getAppIdString() {
      return Integer.toString(appid);
    }
  }
}