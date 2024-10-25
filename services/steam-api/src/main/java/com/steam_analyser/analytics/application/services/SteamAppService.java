package com.steam_analyser.analytics.application.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.store.SteamAppStore;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class SteamAppService {

  private final SteamAppStore steamAppStore;
  private final Executor taskExecutor;

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

  public CompletableFuture<Void> saveAppAsync(SteamAppModel app) {
    return CompletableFuture.runAsync(() -> steamAppStore.save(app), taskExecutor);
  }
}
