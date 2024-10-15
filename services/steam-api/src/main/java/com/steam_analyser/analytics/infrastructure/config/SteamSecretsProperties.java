package com.steam_analyser.analytics.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "steam-config")
public class SteamSecretsProperties {

  private String key;
}
