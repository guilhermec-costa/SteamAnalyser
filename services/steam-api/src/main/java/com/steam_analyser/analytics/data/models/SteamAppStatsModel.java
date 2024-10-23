package com.steam_analyser.analytics.data.models;

import jakarta.persistence.CascadeType;
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
@Table(name = "steam_app_current_stats")
public class SteamAppStatsModel extends BaseModel {
 
  @Column
  private Integer currentPlayers;

  @Column
  private Integer _24hpeak;

  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "local_steam_app_id")
  private SteamAppModel steamApp;

  public void updateCurrentPlayers(Integer playerCount) {
    this.currentPlayers = playerCount;
  }
}
