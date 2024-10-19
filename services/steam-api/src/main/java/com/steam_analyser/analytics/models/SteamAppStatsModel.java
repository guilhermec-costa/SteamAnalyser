package com.steam_analyser.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table
public class SteamAppStatsModel extends BaseModel {
 
  @Column
  private Integer currentPlayers;

  @Column
  private Integer _24hpeak;

  @OneToOne
  @JoinColumn(name = "steam_app_id")
  private SteamAppModel steamApp;
}
