package com.steam_analyser.analytics.api.controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.SteamStoreAPIClient;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.AppDetailsResponse;
import com.steam_analyser.analytics.api.presentation.AppDetailsResponse.GameData;
import com.steam_analyser.analytics.api.presentation.SteamCharts.SteamResponseDTO;
import com.steam_analyser.analytics.api.presentation.SteamCharts.SteamResponseDTO.Rank;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steam/charts")
public class SteamChartsController {
  
  private final WebSteamAPIClient steamClient;
  private final SteamStoreAPIClient steamStoreclient;
  private final SteamSecretsProperties steamSecrets;
  private final ObjectMapper objectMapper;
  
  @GetMapping()
  public List<GameData> mostPlayedGames() throws Exception {
    var result = steamClient.mostPlayedGames(steamSecrets.getKey());
    SteamResponseDTO convertedResponse = objectMapper.readValue(result, SteamResponseDTO.class);
    List<Rank> ranks = convertedResponse.getResponse().getRanks();
    var gamesData = new ArrayList<GameData>();
    for(int i = 0; i<5; i++) {
        var appDetails = steamStoreclient.appDetails(ranks.get(i).getAppid());
        AppDetailsResponse parsedAppDetails = objectMapper.readValue(appDetails, AppDetailsResponse.class);
        gamesData.add(parsedAppDetails.getAdditionalProperties().get(ranks.get(i).getAppIdString()).getData());
      }

    return gamesData;
  }
}
