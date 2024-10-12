package com.steam_analyser.analytics.api.presentation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamAppDetailsResponse {


  @JsonIgnoreProperties(ignoreUnknown = true)
  private Map<String, GameDataContainer> additionalProperties = new HashMap<>();

  @JsonAnyGetter
  public Map<String, GameDataContainer> getAdditionalProperties() {
      return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, GameDataContainer value) {
      this.additionalProperties.put(name, value);
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class GameDataContainer {
    private boolean success;
    private GameData data;
  }

  @Getter
  @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class GameData {

    @JsonIgnore
    private String type;

    private String name;

    @Getter @Setter
    private int playersOnline;

    @Getter @Setter
    private int peakInGame;

    @JsonProperty(value = "steam_appid")
    @JsonIgnore
    private int steamAppId;

    @JsonProperty(value = "required_age")
    @JsonIgnore
    private int requiredAge;

    @JsonProperty(value = "is_free")
    @JsonIgnore
    private boolean isFree;

    @JsonIgnore
    private List<Integer> dlc;

    @JsonProperty(value = "header_image")
    private String headerImage;
  }

  public GameData getGameDataForApp(String appId) {
    return getAdditionalProperties().get(appId).getData();
  }

}
