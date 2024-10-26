package com.steam_analyser.analytics.api.presentation.responses;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamAppStatsResponse {

  @JsonProperty("updated_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  @JsonProperty("_24peak")
  private int _24peak;

  @JsonProperty("current_players")
  private Integer currentPlayers;

  @JsonProperty("name")
  private String name;
}