package com.steam_analyser.analytics.application.runnables;

import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.networking.steam3.ProtocolTypes;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

import com.steam_analyser.analytics.application.schedulers.ISteamChron;
import com.steam_analyser.analytics.application.services.SteamConfigManager;
import com.steam_analyser.analytics.application.services.SteamWebAPIProcessor;

import java.util.List;

@Slf4j
public class SteamWebApiRunnable implements Runnable {

  private final SteamConfigManager steamConfigManager;
  private boolean isExecuting;
  private CallbackManager cbManager;
  private Scanner scanner;
  private SteamWebAPIProcessor steamWebAPIService;
  private List<ISteamChron> futureChrons;

  public SteamWebApiRunnable(
      List<ISteamChron> steamChrons,
      SteamWebAPIProcessor steamWebAPIService,
      SteamConfigManager steamConfigManager) {
    this.scanner = new Scanner(System.in);
    futureChrons = steamChrons;
    this.steamWebAPIService = steamWebAPIService;
    this.steamConfigManager = steamConfigManager;
  }

  @Override
  public void run() {
    steamConfigManager.configureDefaultsWithConnectionType(ProtocolTypes.WEB_SOCKET);
    cbManager = new CallbackManager(steamConfigManager.getClient());

    cbManager.subscribe(ConnectedCallback.class, this::onConnected);
    cbManager.subscribe(DisconnectedCallback.class, this::onDisconnected);
    cbManager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
    cbManager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

    isExecuting = true;
    log.info("Connecting to steam client...");
    steamConfigManager.connectClient();

    while (isExecuting) {
      cbManager.runWaitCallbacks(1000L);
    }

    scanner.close();
  }

  private void onConnected(ConnectedCallback callback) {
    var authToken = steamConfigManager.getAuthToken();
    log.info("Connected to Steam Client");

    if (authToken != null && !authToken.isEmpty()) {
      log.info("Using stored auth token");
      steamConfigManager.logOnWithToken(authToken);
      return;
    }

    log.info("Loggin in with username and password");
    steamConfigManager.authenticateWithCredentials();
  }

  private void onDisconnected(DisconnectedCallback callback) {
    log.info("Disconnected from Steam Servers");

    if (callback.isUserInitiated()) {
      isExecuting = false;
    } else {
      try {
        Thread.sleep(2000L);
        steamConfigManager.connectClient();
      } catch (InterruptedException e) {
        log.error("An Interrupted exception occurred. " + e.getMessage());
      }
    }
  }

  private void onLoggedOn(LoggedOnCallback callback) {
    if (callback.getResult() != EResult.OK) {
      log.error("Unable to logon to Steam: " + callback.getResult() + " / " + callback.getExtendedResult());
      isExecuting = false;
      return;
    }

    log.info("Successfully logged on!");
    steamWebAPIService.setSteamConfiguration(steamConfigManager.getClient().getConfiguration());
    log.info("Starting chron jobs");
    for (var chron : futureChrons) {
      chron.start();
    }
  }

  private void onLoggedOff(LoggedOffCallback callback) {
    log.info("Logged off of Steam: " + callback.getResult());
    isExecuting = false;
  }
}
