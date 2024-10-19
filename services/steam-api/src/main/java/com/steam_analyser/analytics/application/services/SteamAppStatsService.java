package com.steam_analyser.analytics.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.infra.dataAccessors.SteamAppStatsAccessor;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsService {

  private final SteamAppStatsAccessor steamAppStatsRepository;

  public Optional<SteamAppStatsModel> findAppStatsByAppId(String appId) {
    return steamAppStatsRepository.findById(Long.parseLong(appId));
  }

  public SteamAppStatsModel save(SteamAppStatsModel appStat) {
    return steamAppStatsRepository.save(appStat);
  }
}
