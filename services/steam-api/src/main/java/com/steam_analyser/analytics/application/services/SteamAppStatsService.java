package com.steam_analyser.analytics.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.domain.entities.SteamAppStats;
import com.steam_analyser.analytics.domain.repositoryInterfaces.SteamAppStatsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsService {

  private final SteamAppStatsRepository steamAppStatsRepository;

  public Optional<SteamAppStats> findAppStatsByAppId(String appId) {
    return steamAppStatsRepository.findById(Long.parseLong(appId));
  }

  public SteamAppStats save(SteamAppStats appStat) {
    return steamAppStatsRepository.save(appStat);
  }
}
