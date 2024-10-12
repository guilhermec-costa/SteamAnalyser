package com.steam_analyser.analytics.api.externalClients;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface SteamStoreAPIClient {
  
  @GetExchange("appdetails/")
  public String appDetails(@RequestParam(name = "appids") String appIds);
}
