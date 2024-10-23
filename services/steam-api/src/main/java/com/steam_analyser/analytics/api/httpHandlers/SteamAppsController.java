package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.Responses.SteamAppResponse;
import com.steam_analyser.analytics.application.services.ModelMappingService;
import com.steam_analyser.analytics.application.services.SteamAppService;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("steamApps")
public class SteamAppsController {
  
  private final SteamAppService steamAppService;
  private final ModelMappingService modelMappingService;

  @GetMapping("all")
  public ResponseEntity<List<SteamAppResponse>> allAppsInformation() throws Exception {
    var response = steamAppService.findAllSteamApps();
    var parsedResponse = modelMappingService.mapList(response, SteamAppResponse.class);
    return ResponseEntity.ok().body(parsedResponse);
  }
}
