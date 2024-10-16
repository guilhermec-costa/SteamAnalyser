package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.application.schedulers.Synchronizer;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.application.services.WebSteamAPIClientService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("userStats")
public class UserStatsController {

  private final WebSteamAPIClientService webSteamAPIClientService;
  private final Synchronizer synchronizer;
  
  @GetMapping("currentPlayers")
  public ResponseEntity<Integer> numberOfCurrentPlayersByApp(@RequestParam String appId) throws Exception {
    Integer response = webSteamAPIClientService.getNumberOfCurrentPlayersForApp(appId);
    synchronizer.synchronizeAppStats();
    return ResponseEntity.ok().body(response);
  }
}
