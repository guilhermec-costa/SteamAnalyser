package com.steam_analyser.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "steam_app")
public class SteamAppModel extends BaseModel {
  
  @Column
  private String name;

  @Column
  private String externalAppId;
}
