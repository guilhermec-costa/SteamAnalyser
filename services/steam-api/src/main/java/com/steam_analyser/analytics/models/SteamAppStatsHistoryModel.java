package com.steam_analyser.analytics.models;

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
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "steam_app_stats_history")
public class SteamAppStatsHistoryModel extends BaseModel {
 
  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "steam_app_id", unique = false)
  private SteamAppModel steamApp;

  @Column(nullable = true)
  private Integer playerCount;

  @Column(unique = false)
  private LocalDateTime snapshoted_at;
}
