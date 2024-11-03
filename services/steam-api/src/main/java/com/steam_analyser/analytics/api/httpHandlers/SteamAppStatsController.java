package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.responses.AppHistoryResponse;
import com.steam_analyser.analytics.api.presentation.responses.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.api.presentation.responses.SteamAppStatsResponse;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppStatsHistoryService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamAppStats")
public class SteamAppStatsController {

  private final SteamAppStatsService steamAppStatsService;
  private final SteamAppStatsHistoryService steamAppStatsHistoryService;
  private final ModelMappingService modelMappingService;

  @GetMapping("currentPlayers")
  public ResponseEntity<SteamAppPlayerCountResponse> numberOfCurrentPlayersByApp(@RequestParam Long localSteamAppId)
      throws Exception {
    Integer playerCount = steamAppStatsService.currentPlayersForApp(localSteamAppId);
    var response = new SteamAppPlayerCountResponse(playerCount);
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("top")
  public ResponseEntity<Page<SteamAppStatsResponse>> getTopAppsByCurrentPlayers(Pageable pageable) {
    var appsStats = steamAppStatsService.queryTopApps(pageable);
    var parsedResponse = modelMappingService.mapList(appsStats.toList(), SteamAppStatsResponse.class);
    var page = new PageImpl<>(parsedResponse, pageable, parsedResponse.size());
    return ResponseEntity.ok().body(page);
  }

  @GetMapping("appHistory")
  public ResponseEntity<List<AppHistoryResponse>> getAppHistory(@RequestParam Long localSteamAppId) {
    var appHistory = steamAppStatsHistoryService.queryAppHistory(localSteamAppId);
    var parsedHistories = modelMappingService.mapList(appHistory, AppHistoryResponse.class);
    return ResponseEntity.ok().body(parsedHistories);
  }
}
