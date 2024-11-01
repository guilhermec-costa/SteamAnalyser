package com.steam_analyser.analytics.application.services;

import org.springframework.stereotype.Service;

import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.util.compat.Consumer;

@Service
public class GlobalCallbackDispatcher {

  private CallbackManager cbManager;
  private boolean isExecuting = false;
  private final long cbInterval = 1000L;

  public GlobalCallbackDispatcher(SteamConfigManager steamConfigManager) {
    cbManager = new CallbackManager(steamConfigManager.getGlobalClient());
  }

  public void whenLoggedOn(Consumer<LoggedOnCallback> fn) {
    cbManager.subscribe(LoggedOnCallback.class, fn);
  }

  public void whenLoggedOf(Consumer<LoggedOffCallback> fn) {
    cbManager.subscribe(LoggedOffCallback.class, fn);
  }

  public void whenConnected(Consumer<ConnectedCallback> fn) {
    cbManager.subscribe(ConnectedCallback.class, fn);
  }

  public void whenDisconnected(Consumer<DisconnectedCallback> fn) {
    cbManager.subscribe(DisconnectedCallback.class, fn);
  }

  public void mainLoop() {
    while (isExecuting) {
      cbManager.runWaitCallbacks(cbInterval);
    }
  }

  public void enableCallbacksExecution() {
    isExecuting = true;
  }

  public void disableCallbacksExecution() {
    isExecuting = false;
  }
}
