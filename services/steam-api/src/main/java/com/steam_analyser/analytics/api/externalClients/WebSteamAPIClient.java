package com.steam_analyser.analytics.api.externalClients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import com.steam_analyser.analytics.api.presentation.SteamCharts.SteamResponseDTO;

public interface WebSteamAPIClient {

  // ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?
  // ISteamUserStats/GetGlobalStatsForGame/v1/?

  @GetExchange("ISteamUserStats/GetNumberOfCurrentPlayers/v1/")
  public Object numberOfCurrentPlayersByApp(@RequestParam String key, @RequestParam Integer appid);
  // ISteamUserStats/GetPlayerAchievements/v1/?
  // ISteamUserStats/GetSchemaForGame/v2/?
  // ISteamUserStats/GetUserStatsForGame/v2/
  // ISteamUserStats/SetUserStatsForGame/v1/

  @GetExchange("ISteamChartsService/GetMostPlayedGames/v1/")
  public String mostPlayedGames(@RequestParam String key);
}
