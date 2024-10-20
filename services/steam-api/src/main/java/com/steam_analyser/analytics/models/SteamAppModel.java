package com.steam_analyser.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "steam_app")
public class SteamAppModel extends BaseModel {
  
  @Column
  private String name;

  @Column
  private String steamAppId;
}
