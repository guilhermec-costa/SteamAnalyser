package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.SteamResponses.AppListResponse;
import com.steam_analyser.analytics.application.services.WebSteamAPIClientService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamApps")
public class SteamAppsController {
  
  private final WebSteamAPIClientService webSteamAPIClientService;

  @GetMapping("all")
  public ResponseEntity<AppListResponse> allAppsInformation() throws Exception {
    var response = webSteamAPIClientService.getAllApps();
    return ResponseEntity.ok().body(response);
  }
}
