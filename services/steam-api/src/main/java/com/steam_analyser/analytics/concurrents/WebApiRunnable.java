package com.steam_analyser.analytics.concurrents;

import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.steam.webapi.WebAPI;
import in.dragonbra.javasteam.types.KeyValue;
import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;

import java.util.HashMap;
import java.util.Map;

public class WebApiRunnable implements Runnable {

  private SteamClient steamClient;
  private boolean isExecuting;
 
  @Override
  public void run() {
    LogManager.addListener(new DefaultLogListener());

    steamClient = new SteamClient();

    CallbackManager cbManager = new CallbackManager(steamClient);
    cbManager.subscribe(ConnectedCallback.class, this::onConnected);
    cbManager.subscribe(DisconnectedCallback.class, this::onDisconnected);

    cbManager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
    cbManager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

    isExecuting = true;

    steamClient.connect();

    while(isExecuting) {
      cbManager.runWaitAllCallbacks(1000);
    }
  }

  private void onConnected(ConnectedCallback callback) {
    WebAPI api = steamClient.getConfiguration().getWebAPI("iISteamChartsService");
    try {
      Map<String, String> args = new HashMap<>();
      args.put("appid", "730");

      KeyValue result = api.call("ISteamUserStats/GetNumberOfCurrentPlayers", 1, args);
      System.out.println(result);

    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  private void onDisconnected(DisconnectedCallback callback) {
    System.out.println("disconnected to steam servers");
  } 

  private void onLoggedOn(LoggedOnCallback callback) {

  }

  private void onLoggedOff(LoggedOffCallback callback) {

  }
}
