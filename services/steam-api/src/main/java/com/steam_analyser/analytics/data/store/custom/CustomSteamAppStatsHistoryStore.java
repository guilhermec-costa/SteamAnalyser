package com.steam_analyser.analytics.data.store.custom;

public interface CustomSteamAppStatsHistoryStore {
  int deleteExpiredHistoriesFromInterval(int numberOfDays);
}
