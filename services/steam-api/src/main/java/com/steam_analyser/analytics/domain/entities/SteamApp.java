package com.steam_analyser.analytics.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "steam_app")
public class SteamApp extends BaseEntity {
  
  @Column
  private String name;

  @Column
  private String externalAppId;
}
