package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.SteamStoreAPIClient;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse;
import com.steam_analyser.analytics.api.presentation.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.api.presentation.SteamMostPlayedGamesResponse;
import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WebSteamAPIClientService {

  // private final WebSteamAPIClient steamClient;
  private final SteamSecretsProperties steamSecrets;
  private final SteamStoreAPIClient steamStoreClient;
  private final ObjectMapper objectMapper;

  public SteamMostPlayedGamesResponse getMostPlayedApps() throws Exception {
    // String mostPlayedGames = steamClient.mostPlayedGames(steamSecrets.getKey());
    // SteamMostPlayedGamesResponse parsedMostPlayedGames = objectMapper.readValue(mostPlayedGames,
    //     SteamMostPlayedGamesResponse.class);
    // return parsedMostPlayedGames;
    return null;
  }

  public Integer getNumberOfCurrentPlayersForApp(String appId) throws JsonProcessingException, JsonMappingException {
    // try {
    //   var appCurrentPlayersString = steamClient.numberOfCurrentPlayersByApp(steamSecrets.getKey(), appId);
    //   SteamAppPlayerCountResponse appPlayerCountConverted = objectMapper.readValue(appCurrentPlayersString,
    //       SteamAppPlayerCountResponse.class);
    //   return appPlayerCountConverted.getPlayerCount();
    // } catch (Exception e) {
    //   System.out.println(e.getMessage());
    //   return null;
    // }
    return null;
  }

  public SteamAppDetailsResponse getDetailsForApp(final String appId) throws Exception {
    // String appDetails = steamStoreClient.appDetails(appId);
    // SteamAppDetailsResponse parsedAppDetails = objectMapper.readValue(appDetails, SteamAppDetailsResponse.class);
    // return parsedAppDetails;
    return null;
  }

  public AppListResponse getAllApps() throws Exception {
    // String stringfiedApps = steamClient.allApps(steamSecrets.getKey());
    // AppListResponse parsedAppsInformation = objectMapper.readValue(stringfiedApps, AppListResponse.class);
    // return parsedAppsInformation;
    return null;
  }
}
