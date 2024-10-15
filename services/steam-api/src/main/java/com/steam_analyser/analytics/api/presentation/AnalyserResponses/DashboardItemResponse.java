package com.steam_analyser.analytics.api.presentation.AnalyserResponses;

import lombok.Data;

@Data
public class DashboardItemResponse {
  
  private int steamAppId;
  private String name;
  private String playersOnline;
  private int playersOnlineNumber;
  private int peakInGameNumber;
  private String peakInGame;
  private String gameHeaderImage;

}
