package com.steam_analyser.analytics.api.httpHandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steam_analyser.analytics.api.presentation.requests.LoginRequest;
import com.steam_analyser.analytics.application.services.SteamConfigManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("auth")
public class AuthController {
  
  private final SteamConfigManager steamConfigManager;

  @PostMapping("login")
  public ResponseEntity<HttpStatus> login(@RequestBody LoginRequest request) {
    steamConfigManager.defaultAuthentication(request);
    return ResponseEntity.ok().build();
  }
}
