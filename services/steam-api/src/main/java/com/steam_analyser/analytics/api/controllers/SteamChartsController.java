package com.steam_analyser.analytics.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.AnalyserResponses.DashboardItemResponse;
import com.steam_analyser.analytics.api.presentation.SteamAppDetailsResponse.GameData;
import com.steam_analyser.analytics.application.services.ObjectMappingService;
import com.steam_analyser.analytics.application.services.UserChartsService;
import com.steam_analyser.analytics.infrastructure.util.DataUtilities;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamCharts")
public class SteamChartsController {
  
  private final UserChartsService userChartsService; 
  private final ObjectMappingService mappingService;
  
  @GetMapping("mostPlayed")
  public ResponseEntity<List<DashboardItemResponse>> mostPlayedGames() throws Exception {
    List<GameData> dashboardData = userChartsService.genDashboardDataset();
    List<DashboardItemResponse> convertedDashboardDta = mappingService.mapList(dashboardData, DashboardItemResponse.class);
    var orderedDashboardData = DataUtilities.sortDatasetBy(convertedDashboardDta, "playersOnlineNumber").reversed();
    return ResponseEntity.ok().body(orderedDashboardData);
  }
}
