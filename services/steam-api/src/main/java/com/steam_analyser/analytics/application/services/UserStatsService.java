package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserStatsService {

  private final WebSteamAPIClient steamClient;
  private final SteamSecretsProperties steamSecrets;
  private final ObjectMapper objectMapper;

  public SteamAppPlayerCountResponse getNumberOfCurrentPlayersForApp(String appId) throws JsonProcessingException, JsonMappingException {
    var appCurrentPlayersString = steamClient.numberOfCurrentPlayersByApp(steamSecrets.getKey(), appId);
    SteamAppPlayerCountResponse appPlayerCountConverted = objectMapper.readValue(appCurrentPlayersString, SteamAppPlayerCountResponse.class);
    return appPlayerCountConverted;
  }
}
