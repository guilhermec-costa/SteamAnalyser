package com.steam_analyser.analytics.api.controllers;

import java.util.List;
import java.util.LinkedHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.SteamStoreAPIClient;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.AppDetailsResponse;
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
  public SteamResponseDTO mostPlayedGames() throws Exception {
    var result = steamClient.mostPlayedGames(steamSecrets.getKey());
    SteamResponseDTO convertedResponse = objectMapper.readValue(result, SteamResponseDTO.class);
    List<Rank> ranks = convertedResponse.getResponse().getRanks();
    for(Rank rank : ranks) {
      if(rank.getAppid() == 730) {
        var appDetails = steamStoreclient.appDetails(rank.getAppid());
        var data = ((LinkedHashMap<String, Object>)appDetails).get("730");
        System.out.println("data");
        // AppDetailsResponse parsedAppDetails = objectMapper.readValue(data, AppDetailsResponse.class);
        // // System.out.println(parsedAppDetails.getGameData().getName());
        // System.out.println(parsedAppDetails.isSuccess());
      }
    }

    return convertedResponse;
  }
}
