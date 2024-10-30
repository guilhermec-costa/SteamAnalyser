package com.steam_analyser.analytics.application.services;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.steam_analyser.analytics.data.models.SteamAppModel;
import com.steam_analyser.analytics.data.models.SteamAppStatsModel;
import com.steam_analyser.analytics.data.projections.PriorityAppsProjection;
import com.steam_analyser.analytics.data.projections.SteamAppStatsProjection;
import com.steam_analyser.analytics.data.store.SteamAppStatsStore;
import com.steam_analyser.analytics.data.types.PriorityApp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Transactional
public class SteamAppStatsService {

  private final SteamAppStatsStore steamAppStatsStore;
  private final SteamAppStatsHistoryService steamAppStatsHistoryService;

  @PersistenceContext
  private EntityManager entityManager;

  public Optional<SteamAppStatsModel> findAppStatsByAppRegisterId(Long localSteamAppId) {
    return steamAppStatsStore.findBySteamAppId(localSteamAppId);
  }

  public SteamAppStatsModel saveOne(SteamAppStatsModel appStat) {
    return steamAppStatsStore.save(appStat);
  }

  public SteamAppStatsModel findOrCreateStatsModelInstance(SteamAppModel app) {
    return findAppStatsByAppRegisterId(app.getId())
        .orElse(
            SteamAppStatsModel.builder().steamApp(app).currentPlayers(0)
                .build());
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
      saveOne(appInstance);
    }
  }

  public Integer currentPlayersForApp(final Long localSteamAppId) {
    SteamAppStatsModel app = steamAppStatsStore.findBySteamAppId(localSteamAppId)
        .orElseThrow(() -> new IllegalArgumentException("App does not exist"));

    return app.getCurrentPlayers();
  }

  public Page<SteamAppStatsProjection> presentAppsStats(Pageable pageable) {
    return steamAppStatsStore.presentAppsStatsQuery(pageable);
  }

  private final float MIN_ANOMALOUS_COUNT_PCT = 0.15F;
  private final float MAX_ANOMALOUS_COUNT_PCT = 1.95F;

  public boolean canUpdateAppStatsPlayerCount(final Integer lastCount, Integer newCount) {
    return (newCount != null && (lastCount == 0
        || (newCount >= lastCount * MIN_ANOMALOUS_COUNT_PCT && newCount <= lastCount * MAX_ANOMALOUS_COUNT_PCT)));
  }

  public Page<PriorityAppsProjection> queryByPlayersPriority(Pageable pageable) {
    return steamAppStatsStore.findByPlayersPriority(pageable);
  }

  public List<PriorityApp> queryByPlayersPriorityOffset(int offset, int limit) {
    final var query = """
        select sacs.current_players, sa.steam_app_id, sa.id from steam_app_current_stats sacs
        inner join steam_app sa on sa.id = sacs.local_steam_app_id
        order by sacs.current_players desc
        offset ?1
        limit ?2
        """;
    ;

    @SuppressWarnings("unchecked")
    List<Object[]> rows = entityManager.createNativeQuery(query)
        .setParameter(1, offset)
        .setParameter(2, limit)
        .getResultList();

    List<PriorityApp> parsedApps = new ArrayList<>();
    for (Object[] row : rows) {
      Integer playerCount = (Integer) row[0];
      Integer steamAppId = (Integer) row[1];
      Long localSteamAppId = (Long) row[2];
      parsedApps.add(new PriorityApp(playerCount, steamAppId, localSteamAppId));
    }

    return parsedApps;
  }
}
