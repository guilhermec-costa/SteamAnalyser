package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.events.datatypes.PartialSteamAppStatsHistory;
import com.steam_analyser.analytics.infra.dataAccessors.SteamAppStatsHistoryAcessor;
import com.steam_analyser.analytics.models.SteamAppStatsHistoryModel;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class SteamAppStatsHistoryService {

  private final SteamAppStatsHistoryAcessor steamAppStatsHistoryAcessor;

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
    return steamAppStatsHistoryAcessor.queryApp24hPeak(localSteamAppId);
  }

  public void saveMultiple(final List<SteamAppStatsHistoryModel> list) {
    steamAppStatsHistoryAcessor.saveAll(list);
  }
}
