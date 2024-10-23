package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.Responses.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("userStats")
public class UserStatsController {

  private final SteamAppStatsService steamAppStatsService;
  
  @GetMapping("currentPlayers")
  public ResponseEntity<SteamAppPlayerCountResponse> numberOfCurrentPlayersByApp(@RequestParam Long localSteamAppId) throws Exception {
    Integer playerCount = steamAppStatsService.currentPlayersForApp(localSteamAppId);
    var response = new SteamAppPlayerCountResponse(playerCount);
    return ResponseEntity.ok().body(response);
  }
}
