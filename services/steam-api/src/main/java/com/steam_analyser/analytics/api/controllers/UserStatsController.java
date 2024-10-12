package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.application.services.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("userStats")
public class UserStatsController {

  private final UserStatsService userStatsService;
  
  @GetMapping("currentPlayers")
  public ResponseEntity<SteamAppPlayerCountResponse> numberOfCurrentPlayersByApp(@RequestParam String appId) throws Exception {
    SteamAppPlayerCountResponse response = userStatsService.getNumberOfCurrentPlayersForApp(appId);
    return ResponseEntity.ok().body(response);
  }
}
