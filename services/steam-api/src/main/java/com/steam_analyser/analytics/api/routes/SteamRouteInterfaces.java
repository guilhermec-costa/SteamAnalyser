package com.steam_analyser.analytics.api.routes;

public enum SteamRouteInterfaces {

  ISteamUserStats("ISteamUserStats"),
  ISteamApps("ISteamApps");

  public String string;

  SteamRouteInterfaces(final String name) {
    this.string = name;
  }
}
