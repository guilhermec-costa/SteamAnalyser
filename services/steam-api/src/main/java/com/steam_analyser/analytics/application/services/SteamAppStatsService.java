package com.steam_analyser.analytics.application.services;

import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.api.presentation.responses.SteamAppStatsResponse;
import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.projections.SteamAppStatsProjection;
import com.steam_analyser.analytics.data.store.SteamAppStatsStore;
import com.steam_analyser.analytics.data.types.SortableByOptions;
import com.steam_analyser.analytics.util.DataUtilities;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SteamAppStatsService {

  private final SteamAppStatsStore steamAppStatsStore;
  private final SteamAppStatsHistoryService steamAppStatsHistoryService;

  public Optional<SteamAppStatsModel> findAppStatsByAppRegisterId(Long localSteamAppId) {
    return steamAppStatsStore.findBySteamAppId(localSteamAppId);
  }

  public SteamAppStatsModel save(SteamAppStatsModel appStat) {
    return steamAppStatsStore.save(appStat);
  }

  public SteamAppStatsModel findOrCreateStatsModelInstance(SteamAppModel app) {
    return findAppStatsByAppRegisterId(app.getId())
        .orElse(SteamAppStatsModel.builder().steamApp(app).build());
  }

  public void saveMultiple(List<SteamAppStatsModel> list) {
    steamAppStatsStore.saveAll(list);
  }

  public void updateApp24Peak(Long localAppId) {
    var app = findAppStatsByAppRegisterId(localAppId);
    if (app.isPresent()) {
      var appInstance = app.get();
      Integer _24peak = steamAppStatsHistoryService.queryApp24peak(localAppId);
      appInstance.set_24hpeak(_24peak);
      save(appInstance);
    }
  }

  public Integer currentPlayersForApp(final Long localSteamAppId) {
    SteamAppStatsModel app = steamAppStatsStore.findBySteamAppId(localSteamAppId)
        .orElseThrow(() -> new IllegalArgumentException("App does not exist"));

    return app.getCurrentPlayers();
  }

  public List<SteamAppStatsProjection> listBy(Pageable pageable, SortableByOptions by) {
    var result = steamAppStatsStore.presentableAppStats(pageable);
    var sortedDataset = result.toList().stream()
        .sorted(Comparator.comparingInt(SteamAppStatsProjection::getCurrentPlayers));
    return sortedDataset.collect(Collectors.toList()).reversed();
  }
}
