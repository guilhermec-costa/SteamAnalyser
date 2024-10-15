package com.steam_analyser.analytics.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;

@Configuration
public class WebSteamAPIClientConfig {
  
  private static int maxBytesForBuffer = 16 * 1024 * 1024;

  @Bean
  WebSteamAPIClient webSteamAPIClient() {
    WebClient client = WebClient.builder()
      .baseUrl("https://api.steampowered.com")
      .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxBytesForBuffer))
      .build();

    var httpFactory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
    return httpFactory.createClient(WebSteamAPIClient.class);
  }
}
