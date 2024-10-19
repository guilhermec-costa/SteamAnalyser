package com.steam_analyser.analytics.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.steam_analyser.analytics.api.externalClients.SteamStoreAPIClient;

@Configuration
public class SteamStoreAPIClientConfig {
 
  @Bean
  SteamStoreAPIClient steamStoreClient() {
    WebClient client = WebClient.builder()
      .baseUrl("https://store.steampowered.com/api/")
      .build();

      var httpFactory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
      return httpFactory.createClient(SteamStoreAPIClient.class);
  } 
}
