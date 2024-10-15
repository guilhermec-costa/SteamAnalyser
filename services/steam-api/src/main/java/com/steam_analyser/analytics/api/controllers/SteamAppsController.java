package com.steam_analyser.analytics.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse;
import com.steam_analyser.analytics.application.services.SteamAppService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamApps")
public class SteamAppsController {
  
  private final SteamAppService steamAppService;

  @GetMapping("all")
  public ResponseEntity<AppListResponse> allAppsInformation() throws Exception {
    var response = steamAppService.getAllApps();
    return ResponseEntity.ok().body(response);
  }
}
