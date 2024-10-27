package com.steam_analyser.analytics.application.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.api.clients.CloudflareClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CloudFlareService {

  private final CloudflareClient cloudflareClient;

  @Value("${clients.cloudflare.baseurl}")
  private String cloudflareBaseUrl;

  public byte[] retrieveAppImage(final Integer appId) {
    try {
      var response = cloudflareClient.getAppHeaderImage(appId);
      return response.getBody();
    } catch (Exception e) {
      log.error("Failed to get image for app " + appId);
      return null;
    }
  }

  public String getClientUrl() {
    return this.cloudflareBaseUrl;
  }

  public String buildAppHeaderUrl(final Integer appId) {
    StringBuilder baseAppHeader = new StringBuilder(cloudflareBaseUrl + "steam/apps/");
    baseAppHeader.append(appId.toString());
    baseAppHeader.append("/header.jpg");
    return baseAppHeader.toString();
  }
}
