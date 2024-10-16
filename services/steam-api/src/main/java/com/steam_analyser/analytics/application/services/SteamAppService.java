package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.domain.entities.SteamApp;
import com.steam_analyser.analytics.domain.repositoryInterfaces.SteamAppRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppService {

  private final SteamAppRepository steamAppRepository;

  public List<SteamApp> getAllSteamApps() {
    return steamAppRepository.findAll();
  }

  public void saveApps(List<SteamApp> appsToSave) {
    steamAppRepository.saveAll(appsToSave);
  }

  public SteamApp findAppById(String appId) {
    return steamAppRepository.findById(Long.parseLong(appId))
      .orElseThrow();
  }
}
