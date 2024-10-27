package com.steam_analyser.analytics.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.steam_analyser.analytics.api.clients.CloudflareClient;

@Configuration
public class HttpClientsConfig {

  @Value("${clients.cloudflare.baseurl}")
  private String cloudflareBaseUrl;

  @Bean
  CloudflareClient cloudflareClient() {
    WebClient client = WebClient.builder()
        .baseUrl(cloudflareBaseUrl)
        .build();

    var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
    return factory.createClient(CloudflareClient.class);
  }
}
