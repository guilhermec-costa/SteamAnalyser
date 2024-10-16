package com.steam_analyser.analytics.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steam_analyser.analytics.api.externalClients.WebSteamAPIClient;
import com.steam_analyser.analytics.api.presentation.SteamAppPlayerCountResponse;
import com.steam_analyser.analytics.domain.entities.SteamAppStats;
import com.steam_analyser.analytics.domain.repositoryInterfaces.SteamAppStatsRepository;
import com.steam_analyser.analytics.infrastructure.config.SteamSecretsProperties;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserStatsService {

  private final SteamAppStatsRepository steamAppStatsRepository;

  public Optional<SteamAppStats> findSteamAppStatsById(String appId) {
    return steamAppStatsRepository.findById(Long.parseLong(appId));
  }
}
