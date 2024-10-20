package com.steam_analyser.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "steam_app_stats")
public class SteamAppStatsModel extends BaseModel {
 
  @Column
  private Integer currentPlayers;

  @Column
  private Integer _24hpeak;

  @OneToOne
  @JoinColumn(name = "steam_app_id")
  private SteamAppModel steamApp;
}
