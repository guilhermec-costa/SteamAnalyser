package com.steam_analyser.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "steam_app_stats_history")
public class SteamAppStatsHistory extends BaseModel {
 
  @OneToOne
  @JoinColumn(name = "steam_app_id")
  private SteamAppModel steamApp;

  @Column
  private String triggeredBy;
}
