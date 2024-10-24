package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.responses.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.api.presentation.responses.SteamAppStatsResponse;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.projections.SteamAppStatsProjection;
import com.steam_analyser.analytics.data.types.SortableByOptions;

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
  private final ModelMappingService modelMappingService;

  @GetMapping("currentPlayers")
  public ResponseEntity<SteamAppPlayerCountResponse> numberOfCurrentPlayersByApp(@RequestParam Long localSteamAppId)
      throws Exception {
    Integer playerCount = steamAppStatsService.currentPlayersForApp(localSteamAppId);
    var response = new SteamAppPlayerCountResponse(playerCount);
    return ResponseEntity.ok().body(response);
  }

  @GetMapping("topBy")
  public ResponseEntity<Page<SteamAppStatsResponse>> getTopByCurrentPlayers(
      @RequestParam(required = false, defaultValue = "currentPlayers") SortableByOptions sortParam,
      Pageable pageable) {
    var appsStats = steamAppStatsService.listBy(pageable, sortParam);
    var parsedResponse = modelMappingService.mapList(appsStats, SteamAppStatsResponse.class);
    var page = new PageImpl<>(parsedResponse, pageable, parsedResponse.size());
    return ResponseEntity.ok().body(page);
  }
}
