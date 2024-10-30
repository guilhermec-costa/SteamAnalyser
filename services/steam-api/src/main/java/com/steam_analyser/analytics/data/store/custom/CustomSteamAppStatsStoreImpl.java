package com.steam_analyser.analytics.data.store.custom;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.data.types.PriorityApp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class CustomSteamAppStatsStoreImpl implements CustomSteamAppStatsStore {

  @PersistenceContext
  private EntityManager entityManager;

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
