package com.steam_analyser.analytics.application.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.infra.dataAccessors.SteamAppAccessor;
import com.steam_analyser.analytics.models.SteamAppModel;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppService {

  private final SteamAppAccessor steamAppAccessor;

  public List<SteamAppModel> findAllSteamApps() {
    return steamAppAccessor.findAll();
  }

  public List<SteamAppModel> findNSteamApps(Pageable pageable) {
    return steamAppAccessor.findNElements(pageable);
  }

  public void saveApps(List<SteamAppModel> appsToSave) {
    steamAppAccessor.saveAll(appsToSave);
  }

  public SteamAppModel findAppById(String appId) {
    return steamAppAccessor.findById(Long.parseLong(appId))
      .orElseThrow();
  }
}
