package com.steam_analyser.analytics.application.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse;
import com.steam_analyser.analytics.api.presentation.SteamMostPlayedGamesResponse;
import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse.GameData;
import com.steam_analyser.analytics.api.presentation.SteamMostPlayedGamesResponse.Rank;
import com.steam_analyser.analytics.infra.util.Formmater;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserChartsService {

  private final WebSteamAPIClientService webSteamAPIClientService;
  private final int maxAppsForDashboard = 20;

  @Cacheable("mostPlayed")
  public List<GameData> genDashboardDataset() throws Exception {
    SteamMostPlayedGamesResponse parsedMostPlayedGames = webSteamAPIClientService.getMostPlayedApps();
    List<Rank> ranks = parsedMostPlayedGames.getResponse().getRanks();
    var gamesData = new ArrayList<GameData>();

    for (int i = 0; i < maxAppsForDashboard; i++) {
      Rank nextRank = ranks.get(i);
      String appIdFromRank = nextRank.getAppIdString();
      Integer currentPlayers = webSteamAPIClientService.getNumberOfCurrentPlayersForApp(appIdFromRank);
      if (currentPlayers == null)
        continue;
      String formmatedPlayersCount = Formmater.formatNumberUsingLocale(currentPlayers);

      SteamAppDetailsResponse parsedAppDetails = webSteamAPIClientService.getDetailsForApp(appIdFromRank);
      GameData parsedAppGameData = parsedAppDetails.getGameDataForApp(appIdFromRank);

      String peakInGame = Formmater.formatNumberUsingLocale((nextRank.getPeakInGame()));

      parsedAppGameData.setPlayersOnline(formmatedPlayersCount);
      parsedAppGameData.setPlayersOnlineNumber(currentPlayers);
      parsedAppGameData.setPeakInGameNumber(nextRank.getPeakInGame());
      parsedAppGameData.setPeakInGame(peakInGame);
      gamesData.add(parsedAppGameData);
    }

    return gamesData;
  }
}
