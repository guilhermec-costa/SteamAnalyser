package com.steam_analyser.analytics.data.store.custom;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomSteamAppStatsHistoryStoreImpl implements CustomSteamAppStatsHistoryStore {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Modifying
  public int deleteExpiredHistoriesFromInterval(int numberOfDays) {
    String query = "delete from steam_app_stats_history " +
        "where snapshoted_at < now() - interval '" + numberOfDays + " days'";

    return entityManager.createNativeQuery(query).executeUpdate();
  }

}
