package com.steam_analyser.analytics.api;

public enum SteamRouteInterfaces {

  ISteamUserStats("ISteamUserStats");

  public String string;

  SteamRouteInterfaces(final String name) {
    this.string = name;
  }
}
