package com.steam_analyser.analytics.application.services;

import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.ISteamApps;
import static com.steam_analyser.analytics.api.routes.SteamRouteInterfaces.ISteamUserStats;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.GetAppList;
import static com.steam_analyser.analytics.api.routes.SteamRouteMethods.GetNumberOfCurrentPlayers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.api.presentation.externalResponses.SingleSteamApp;
import com.steam_analyser.analytics.application.services.abstractions.ICacheService;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class SteamWebAPIService {

  private final int initialBackoff = 500;
  private final int retryLimit = 3;
  private SteamConfiguration steamConfiguration;

  @Qualifier("redisService")
  private final ICacheService cacheService;

  private final SteamSecretsProperties steamSecretsProperties;

  public void setSteamConfiguration(SteamConfiguration config) {
    this.steamConfiguration = config;
  }

  public Integer retryQueryPlayerCountForApp(Integer steamAppId) {
    int currentAttempts = 0;
    int backoff = initialBackoff;

    while (currentAttempts < retryLimit) {
      try {
        Map<String, String> args = new HashMap<>();
        args.put("appid", steamAppId.toString());

        var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string)
            .call("GET", GetNumberOfCurrentPlayers.string, GetNumberOfCurrentPlayers.version, args);

        return extractPlayerCountingFromResponse(currentPlayersResponse);

      } catch (Exception e) {
        try {
          Thread.sleep(backoff);
          backoff *= 2;
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return null;
        }
      }
      currentAttempts++;
    }

    return null;
  }

  public Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }

  public List<SingleSteamApp> fetchAllSteamApps() {
    try {
      var existingsAppsResponse = steamConfiguration.getWebAPI(ISteamApps.string)
          .call("GET", GetAppList.string, GetAppList.version);
      return parseSteamAppsResponse(existingsAppsResponse);
    } catch (Exception e) {
      return List.of();
    }
  }

  public List<SingleSteamApp> parseSteamAppsResponse(KeyValue response) {
    List<SingleSteamApp> parsedApps = new ArrayList<>();
    var appsContainer = response.getChildren().get(0).getChildren();
    for (var app : appsContainer) {
      var appContent = app.getChildren();
      var appId = appContent.get(0).getValue();
      var appName = appContent.get(1).getValue();
      parsedApps.add(new SingleSteamApp(Integer.parseInt(appId), appName));
    }
    return parsedApps;
  }

  public String getAuthToken() {
    return (String) cacheService.get(steamSecretsProperties.getAuthTokenCacheKey());
  }

  public void storeAuthToken(String token) {
    cacheService.set(steamSecretsProperties.getAuthTokenCacheKey(), token);
  }
}
