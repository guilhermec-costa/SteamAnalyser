package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppService {
 
  private final WebSteamAPIClient webSteamClient;
  private final SteamSecretsProperties steamSecrets;
  private final ObjectMapper objectMapper;

  public AppListResponse getAllApps() throws Exception {
    String stringfiedApps = webSteamClient.allApps(steamSecrets.getKey());
    AppListResponse parsedAppsInformation = objectMapper.readValue(stringfiedApps, AppListResponse.class);
    return parsedAppsInformation;
  }
}
