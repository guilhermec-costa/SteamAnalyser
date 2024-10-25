package com.steam_analyser.analytics.api.routes;

public enum SteamRouteMethods {

  GetNumberOfCurrentPlayers("GetNumberOfCurrentPlayers", 1),
  GetAppList("GetAppList", 2);

  public String string;
  public Integer version;

  SteamRouteMethods(String _name, int _version) {
    string = _name;
    version = _version;
  }
}
