package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamCharts.SteamResponseDTO;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steam/charts")
public class SteamChartsController {
  
  private final WebSteamAPIClient steamClient;
  private final SteamSecretsProperties steamSecrets;
  private final ObjectMapper objectMapper;
  
  @GetMapping
  public SteamResponseDTO mostPlayedGames() throws Exception {
    var result = steamClient.mostPlayedGames(steamSecrets.getKey());
    return objectMapper.readValue(result, SteamResponseDTO.class);
  }
}
