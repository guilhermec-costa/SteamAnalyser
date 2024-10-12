package com.steam_analyser.analytics.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse.GameData;
import com.steam_analyser.analytics.application.services.UserChartsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamCharts")
public class SteamChartsController {
  
  private final UserChartsService userChartsService; 
  
  @GetMapping("mostPlayed")
  public ResponseEntity<List<GameData>> mostPlayedGames() throws Exception {
    List<GameData> dashboardData = userChartsService.genDashboardDataset();
    return ResponseEntity.ok().body(dashboardData);
  }
}
