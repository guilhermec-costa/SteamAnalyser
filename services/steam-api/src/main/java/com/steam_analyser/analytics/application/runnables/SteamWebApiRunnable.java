package com.steam_analyser.analytics.application.runnables;

import in.dragonbra.javasteam.enums.EResult;
import in.dragonbra.javasteam.networking.steam3.ProtocolTypes;
import in.dragonbra.javasteam.steam.authentication.AuthPollResult;
import in.dragonbra.javasteam.steam.authentication.AuthSessionDetails;
import in.dragonbra.javasteam.steam.authentication.AuthenticationException;
import in.dragonbra.javasteam.steam.authentication.UserConsoleAuthenticator;
import in.dragonbra.javasteam.steam.discovery.FileServerListProvider;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOffCallback;
import in.dragonbra.javasteam.steam.handlers.steamuser.callback.LoggedOnCallback;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.callbackmgr.CallbackManager;
import in.dragonbra.javasteam.steam.steamclient.callbacks.ConnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.callbacks.DisconnectedCallback;
import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.util.log.LogListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.steam_analyser.analytics.application.chrons.ISteamChron;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;

import java.util.List;

public class SteamWebApiRunnable implements Runnable {

  private SteamClient steamClient;
  private SteamSecretsProperties steamSecrets;
  private boolean isExecuting;
  private final String username;
  private final String password;
  private SteamUser steamUser;
  private CallbackManager cbManager;
  private String previouslyStoredGuardData;
  private String authToken;
  private Scanner scanner;
  private List<ISteamChron> futureChrons;
  private Logger logger = LoggerFactory.getLogger(getClass());

  public SteamWebApiRunnable(SteamSecretsProperties steamSecrets, List<ISteamChron> steamChrons) {
    this.username = steamSecrets.getUsername();
    this.password = steamSecrets.getPassword();
    this.scanner = new Scanner(System.in);
    this.authToken = loadGuardData();
    futureChrons = steamChrons;
    this.steamSecrets = steamSecrets;
  }

  static class MyListener implements LogListener {
    @Override
    public void onLog(Class<?> clazz, String message, Throwable throwable) {
      System.out.println("MyListener - " + clazz.getName() + ": " + message);
    }

    @Override
    public void onError(Class<?> clazz, String message, Throwable throwable) {
      System.err.println("MyListener - " + clazz.getName() + ": " + message);
    }
  }

  @Override
  public void run() {

    SteamConfiguration configuration = SteamConfiguration.create(builder -> {
      builder.withProtocolTypes(ProtocolTypes.WEB_SOCKET);
      builder.withServerListProvider(new FileServerListProvider(new File("servers.bin")));
      builder.withWebAPIKey(this.steamSecrets.getKey());
    });

    steamClient = new SteamClient(configuration);
    cbManager = new CallbackManager(steamClient);
    steamUser = steamClient.getHandler(SteamUser.class);

    cbManager.subscribe(ConnectedCallback.class, this::onConnected);
    cbManager.subscribe(DisconnectedCallback.class, this::onDisconnected);

    cbManager.subscribe(LoggedOnCallback.class, this::onLoggedOn);
    cbManager.subscribe(LoggedOffCallback.class, this::onLoggedOff);

    isExecuting = true;

    logger.info("Connecting to steam client...");

    steamClient.connect();

    while (isExecuting) {
      cbManager.runWaitCallbacks(1000L);
    }

    scanner.close();
  }

  private void onConnected(ConnectedCallback callback) {
    logger.info("Connected to Steam Client");

    if (authToken != null && !authToken.isEmpty()) {
      logger.info("Using stored auth token");
      logOnWithToken(authToken);
    } else {
      logger.info("Loggin in with username and password");
      authenticateWithCredentials();
    }
  }

  private void authenticateWithCredentials() {
    boolean shouldRememberPassword = true;

    AuthSessionDetails authDetails = new AuthSessionDetails();
    authDetails.username = username;
    authDetails.password = password;
    authDetails.persistentSession = shouldRememberPassword;
    authDetails.guardData = previouslyStoredGuardData;
    authDetails.authenticator = new UserConsoleAuthenticator();

    try {
      var authSession = steamClient.getAuthentication().beginAuthSessionViaCredentials(authDetails);
      AuthPollResult pollResponse = authSession.pollingWaitForResultCompat().get();

      if (pollResponse.getNewGuardData() != null) {
        previouslyStoredGuardData = pollResponse.getNewGuardData();
        saveGuardData(pollResponse.getRefreshToken());
      }

      logOnWithToken(pollResponse.getRefreshToken());
    } catch (Exception e) {
      handleAuthenticationException(e);
    }
  }

  private void logOnWithToken(String token) {
    LogOnDetails details = new LogOnDetails();
    details.setUsername(username);
    details.setAccessToken(token);
    details.setLoginID(149);

    steamUser.logOn(details);
  }

  private void onDisconnected(DisconnectedCallback callback) {
    System.out.println("Disconnected from Steam servers");
    logger.info("Disconnected from Steam Servers");

    if (callback.isUserInitiated()) {
      isExecuting = false;
    } else {
      try {
        Thread.sleep(2000L);
        steamClient.connect();
      } catch (InterruptedException e) {
        logger.error("An Interrupted exception occurred. " + e.getMessage());
      }
    }
  }

  private void onLoggedOn(LoggedOnCallback callback) {
    if (callback.getResult() != EResult.OK) {
      logger.error("Unable to logon to Steam: " + callback.getResult() + " / " + callback.getExtendedResult());
      isExecuting = false;
      return;
    }

    logger.info("Successfully logged on!");
    var steamConfiguration = steamClient.getConfiguration();

    logger.info("Starting chron jobs");
    for (var chron : futureChrons) {
      chron.start(steamConfiguration);
    }
  }

  private void onLoggedOff(LoggedOffCallback callback) {
    logger.info("Logged off of Steam: " + callback.getResult());
    isExecuting = false;
  }

  private void saveGuardData(String guardData) {
    try (FileWriter fw = new FileWriter("./" + "guardData.txt")) {
      fw.write(guardData);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String loadGuardData() {
    try (BufferedReader reader = new BufferedReader(new FileReader("./" + "guardData.txt"))) {
      return reader.readLine();
    } catch (IOException e) {
      return null;
    }
  }

  private void handleAuthenticationException(Exception e) {
    if (e instanceof AuthenticationException) {
      logger.error("An Authentication error has occurred. " + e.getMessage());
    } else if (e instanceof CancellationException) {
      logger.error("An Cancellation exception was raised. Usually means a timeout occurred. " + e.getMessage());
    } else {
      logger.error("An error occurred:" + e.getMessage());
    }

    steamUser.logOff();
  }
}
