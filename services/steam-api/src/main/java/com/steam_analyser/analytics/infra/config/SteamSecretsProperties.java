package com.steam_analyser.analytics.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "steam-config")
public class SteamSecretsProperties {

  private String key;
  private String username;
  private String password;
  private String authTokenCacheKey;
}
