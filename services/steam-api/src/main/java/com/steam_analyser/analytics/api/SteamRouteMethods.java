package com.steam_analyser.analytics.api;

public enum SteamRouteMethods {

  GetNumberOfCurrentPlayers("GetNumberOfCurrentPlayers", 1);

  public String string;
  public Integer version;

  SteamRouteMethods(final String _name, final int _version) {
    string = _name;
    version = _version;
  }
}
