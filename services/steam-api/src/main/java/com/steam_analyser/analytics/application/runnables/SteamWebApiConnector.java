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
import com.steam_analyser.analytics.application.services.GlobalCallbackDispatcher;
import com.steam_analyser.analytics.application.services.SteamConfigManager;
import com.steam_analyser.analytics.application.services.SteamWebAPIProcessor;

import java.util.List;

@Slf4j
public class SteamWebApiConnector implements Runnable {

  private final SteamConfigManager steamConfigManager;
  private GlobalCallbackDispatcher globalCallbackDispatcher;
  private Scanner scanner;
  private SteamWebAPIProcessor steamWebAPIService;
  private List<ISteamChron> futureChrons;

  public SteamWebApiConnector(
      List<ISteamChron> steamChrons,
      SteamWebAPIProcessor steamWebAPIService,
      SteamConfigManager steamConfigManager,
      GlobalCallbackDispatcher globalCallbackDispatcher) {
    this.scanner = new Scanner(System.in);
    futureChrons = steamChrons;
    this.steamWebAPIService = steamWebAPIService;
    this.steamConfigManager = steamConfigManager;
    this.globalCallbackDispatcher = globalCallbackDispatcher;
  }

  @Override
  public void run() {
    globalCallbackDispatcher.whenConnected(this::onConnected);
    globalCallbackDispatcher.whenDisconnected(this::onDisconnected);
    globalCallbackDispatcher.whenLoggedOn(this::onLoggedOn);
    globalCallbackDispatcher.whenLoggedOff(this::onLoggedOff);
    globalCallbackDispatcher.enableCallbacksExecution();
    steamConfigManager.connectClient();
    globalCallbackDispatcher.mainLoop();
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
      globalCallbackDispatcher.disableCallbacksExecution();
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
      globalCallbackDispatcher.disableCallbacksExecution();
      return;
    }

    log.info("Successfully logged on!");
    steamWebAPIService.setSteamConfiguration(steamConfigManager.getGlobalClient().getConfiguration());
    log.info("Starting chron jobs");
    for (var chron : futureChrons) {
      chron.start();
    }
  }

  private void onLoggedOff(LoggedOffCallback callback) {
    log.info("Logged off of Steam: " + callback.getResult());
    globalCallbackDispatcher.disableCallbacksExecution();
  }
}
