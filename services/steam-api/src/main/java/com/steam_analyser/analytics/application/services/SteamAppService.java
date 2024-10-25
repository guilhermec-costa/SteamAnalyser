package com.steam_analyser.analytics.application.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.store.SteamAppStore;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppService {

  private final SteamAppStore steamAppStore;

  public List<SteamAppModel> findAllSteamApps() {
    return steamAppStore.findAll();
  }

  public List<SteamAppModel> findNSteamApps(Pageable pageable) {
    return steamAppStore.findNElements(pageable);
  }

  public void saveList(List<SteamAppModel> appsToSave) {
    steamAppStore.saveAll(appsToSave);
  }

  public void saveOne(SteamAppModel app) {
    steamAppStore.save(app);
  }

  public SteamAppModel findAppById(final Long appId) {
    return steamAppStore.findById(appId)
        .orElseThrow(() -> new IllegalArgumentException("App does not exist"));
  }

  public Optional<SteamAppModel> findAppBySteamAppId(final String steamAppId) {
    return steamAppStore.findBySteamAppId(steamAppId);
  }

}
