package com.steam_analyser.analytics.api.externalClients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface WebSteamAPIClient {

  @GetExchange("ISteamUserStats/GetNumberOfCurrentPlayers/v1/")
  public String numberOfCurrentPlayersByApp(@RequestParam String key, @RequestParam String appid);

  @GetExchange("ISteamChartsService/GetMostPlayedGames/v1/")
  public String mostPlayedGames(@RequestParam String key);

  @GetExchange("ISteamApps/GetAppList/v2/")
  public String allApps(@RequestParam String key);

}
