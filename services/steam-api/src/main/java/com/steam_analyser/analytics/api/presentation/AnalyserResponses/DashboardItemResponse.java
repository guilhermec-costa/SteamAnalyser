package com.steam_analyser.analytics.api.presentation.AnalyserResponses;

import lombok.Data;

@Data
public class DashboardItemResponse {
  
  private String name;
  private int playersOnline;
  private int peakInGame;
  private String gameHeaderImage;

}
