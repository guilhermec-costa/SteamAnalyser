package com.steam_analyser.analytics.domain.entities;

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
public class SteamAppStats extends BaseEntity {
 
  @Column
  private Integer currentPlayers;

  @Column
  private Integer _24hpeak;

  @OneToOne
  @JoinColumn(name = "steam_app_id")
  private SteamApp steamApp;
}
