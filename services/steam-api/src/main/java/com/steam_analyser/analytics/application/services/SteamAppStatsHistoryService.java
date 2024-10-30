package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.steam_analyser.analytics.data.models.SteamAppStatsHistoryModel;
import com.steam_analyser.analytics.data.store.SteamAppStatsHistoryStore;
import com.steam_analyser.analytics.data.types.PartialSteamAppStatsHistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class SteamAppStatsHistoryService {

  private final SteamAppStatsHistoryStore steamAppStatsHistoryStore;

  public SteamAppStatsHistoryModel mountFromPartial(final PartialSteamAppStatsHistory data) {
    SteamAppStatsHistoryModel historyInstance = new SteamAppStatsHistoryModel(
        data.getSteamApp(),
        data.getCount(),
        data.getSnapshoted_at());

    return historyInstance;
  }

  public List<SteamAppStatsHistoryModel> mountListFromPartials(final List<PartialSteamAppStatsHistory> data) {
    var list = new ArrayList<SteamAppStatsHistoryModel>();
    for (var el : data) {
      list.add(mountFromPartial(el));
    }
    return list;
  }

  public Integer queryApp24peak(final Long localSteamAppId) {
    return steamAppStatsHistoryStore.queryApp24hPeak(localSteamAppId);
  }

  public void saveMultiple(final List<SteamAppStatsHistoryModel> list) {
    steamAppStatsHistoryStore.saveAll(list);
  }

  public void saveOne(final SteamAppStatsHistoryModel instance) {
    steamAppStatsHistoryStore.save(instance);
  }

  private final int EXPIRATION_DAYS_LIMIT = 7;

  public void purgeExpiredHistories() {
    int affectedRows = steamAppStatsHistoryStore.deleteExpiredHistoriesFromInterval(EXPIRATION_DAYS_LIMIT);
    log.info(affectedRows + " rows deleted from steam_app_stats_history chron");
  }
}
