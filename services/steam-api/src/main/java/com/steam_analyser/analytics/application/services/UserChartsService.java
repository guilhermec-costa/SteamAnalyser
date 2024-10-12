package com.steam_analyser.analytics.application.services;

import org.hibernate.internal.ExceptionConverterImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.SteamStoreAPIClient;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse;
import com.steam_analyser.analytics.api.presentation.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.api.presentation.SteamMostPlayedGamesResponse;
import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse.GameData;
import com.steam_analyser.analytics.api.presentation.SteamMostPlayedGamesResponse.Rank;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserChartsService {

  private final WebSteamAPIClient steamClient;
  private final SteamStoreAPIClient steamStoreClient;
  private final SteamSecretsProperties steamSecrets;
  private final UserStatsService userStatsService;
  private final ObjectMapper objectMapper;

  private final int maxAppsForDashboard = 5;

  @Cacheable("mostPlayed")
  public List<GameData> genDashboardDataset() throws Exception {
    SteamMostPlayedGamesResponse parsedMostPlayedGames = getMostPlayedApps();
    List<Rank> ranks = parsedMostPlayedGames.getResponse().getRanks();
    var gamesData = new ArrayList<GameData>();

    for(int i = 0; i<maxAppsForDashboard; i++) {
      String appIdFromRank = ranks.get(i).getAppIdString();
      SteamAppPlayerCountResponse appPlayerCountResponse = userStatsService.getNumberOfCurrentPlayersForApp(appIdFromRank);
      int playersCount = appPlayerCountResponse.getPlayerCount();

      SteamAppDetailsResponse parsedAppDetails = getDetailsForApp(appIdFromRank);
      GameData parsedAppGameData = parsedAppDetails.getGameDataForApp(appIdFromRank);

      parsedAppGameData.setPlayersOnline(playersCount);
      parsedAppGameData.setPeakInGame(ranks.get(i).getPeakInGame());
      gamesData.add(parsedAppGameData);
    }

    return gamesData;
  }

  public SteamAppDetailsResponse getDetailsForApp(final String appId) throws Exception {
    String appDetails = steamStoreClient.appDetails(appId);
    SteamAppDetailsResponse parsedAppDetails = objectMapper.readValue(appDetails, SteamAppDetailsResponse.class);
    return parsedAppDetails;
  }

  public SteamMostPlayedGamesResponse getMostPlayedApps() throws Exception {
    String mostPlayedGames = steamClient.mostPlayedGames(steamSecrets.getKey());
    SteamMostPlayedGamesResponse parsedMostPlayedGames = objectMapper.readValue(mostPlayedGames, SteamMostPlayedGamesResponse.class);
    return parsedMostPlayedGames;
  }
  
}
