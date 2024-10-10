package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping("user/stats")
public class UserStatsController {

  private final WebSteamAPIClient steamClient;
  private final SteamSecretsProperties steamSecrets;
  
  @GetMapping
  public Object numberOfCurrentPlayersByApp(@RequestParam Integer appId) {
    var result = steamClient.numberOfCurrentPlayersByApp(steamSecrets.getKey(), appId);
    return result;
  }
}
