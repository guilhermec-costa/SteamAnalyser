package com.steam_analyser.analytics.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
@Table(name = "steam_app", indexes = @Index(name = "steam_app_id_idx", columnList = "steam_app_id"))
public class SteamAppModel extends BaseModel {

  @Column
  private String name;

  @Column
  private Integer steamAppId;

  @Column
  private String appImage;
}
