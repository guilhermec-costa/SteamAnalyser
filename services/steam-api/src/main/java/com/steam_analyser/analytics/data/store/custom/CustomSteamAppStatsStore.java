package com.steam_analyser.analytics.data.store.custom;
import java.util.List;

import com.steam_analyser.analytics.data.types.PriorityApp;

public interface CustomSteamAppStatsStore {
  List<PriorityApp> queryByPlayersPriorityOffset(int offset, int limit);
}
