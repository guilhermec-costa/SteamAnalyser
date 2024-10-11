package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("apps")
public class SteamApps {

  private final WebSteamAPIClient steamClient;
  private final SteamSecretsProperties steamSecrets;

  @GetMapping("applist")
  public Object appList() {
    var result = steamClient.infoFromApps(steamSecrets.getKey());
    return result;
  }
}
