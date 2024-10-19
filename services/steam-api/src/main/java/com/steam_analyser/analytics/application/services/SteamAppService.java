package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.infra.dataAccessors.SteamAppAccessor;
import com.steam_analyser.analytics.models.SteamAppModel;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppService {

  private final SteamAppAccessor steamAppRepository;

  public List<SteamAppModel> getAllSteamApps() {
    return steamAppRepository.findAll();
  }

  public void saveApps(List<SteamAppModel> appsToSave) {
    steamAppRepository.saveAll(appsToSave);
  }

  public SteamAppModel findAppById(String appId) {
    return steamAppRepository.findById(Long.parseLong(appId))
      .orElseThrow();
  }
}
