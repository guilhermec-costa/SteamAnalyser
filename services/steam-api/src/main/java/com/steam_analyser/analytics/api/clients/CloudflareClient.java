package com.steam_analyser.analytics.api.clients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface CloudflareClient {

  @GetExchange("steam/apps/{appId}/header.jpg")
  public ResponseEntity<byte[]> getAppHeaderImage(@PathVariable(name = "appId") Integer appId);
}
