package com.steam_analyser.analytics.application.services;

import java.io.File;
import java.util.concurrent.CancellationException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.api.presentation.requests.LoginRequest;
import com.steam_analyser.analytics.application.services.abstractions.ICacheService;
import com.steam_analyser.analytics.data.types.SteamAuthUser;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;

import in.dragonbra.javasteam.networking.steam3.ProtocolTypes;
import in.dragonbra.javasteam.steam.authentication.AuthPollResult;
import in.dragonbra.javasteam.steam.authentication.AuthSessionDetails;
import in.dragonbra.javasteam.steam.authentication.AuthenticationException;
import in.dragonbra.javasteam.steam.authentication.UserConsoleAuthenticator;
import in.dragonbra.javasteam.steam.discovery.FileServerListProvider;
import in.dragonbra.javasteam.steam.handlers.steamuser.LogOnDetails;
import in.dragonbra.javasteam.steam.handlers.steamuser.SteamUser;
import in.dragonbra.javasteam.steam.steamclient.SteamClient;
import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SteamConfigManager {

  private SteamSecretsProperties steamSecrets;
  private ICacheService cacheService;

  @Getter
  private SteamConfiguration steamConfiguration;

  @Getter
  @Setter
  private SteamClient globalClient;

  private SteamAuthUser loggedUser;
  private SteamUser systemSteamUser;

  public SteamConfigManager(
      final SteamSecretsProperties steamSecrets,
      @Qualifier("redisService") final ICacheService cacheService) {
    this.steamSecrets = steamSecrets;
    this.cacheService = cacheService;
    this.loggedUser = new SteamAuthUser(
        steamSecrets.getUsername(),
        steamSecrets.getPassword(),
        "");
    
    configureDefaultsWithConnectionType(ProtocolTypes.WEB_SOCKET);
  }

  public SteamClient generateDefaultClient() {
    return new SteamClient();
  }

  public void configureDefaultsWithConnectionType(ProtocolTypes protocolType) {
    steamConfiguration = SteamConfiguration.create(builder -> {
      builder.withProtocolTypes(protocolType);
      builder.withServerListProvider(new FileServerListProvider(new File("servers.bin")));
      builder.withWebAPIKey(steamSecrets.getKey());
    });

    globalClient = new SteamClient(steamConfiguration);
    systemSteamUser = globalClient.getHandler(SteamUser.class);
  }

  public void logOnWithToken(String token) {
    LogOnDetails details = new LogOnDetails();
    details.setUsername(this.loggedUser.getUsername());
    details.setAccessToken(token);
    details.setLoginID(149);
    systemSteamUser.logOn(details);
  }

  public void authenticateWithCredentials() {
    boolean shouldRememberPassword = true;

    AuthSessionDetails authDetails = new AuthSessionDetails();
    authDetails.username = loggedUser.getUsername();
    authDetails.password = loggedUser.getPassword();
    authDetails.persistentSession = shouldRememberPassword;
    authDetails.guardData = loggedUser.getPreviouslyStoredGuardData();
    authDetails.authenticator = new UserConsoleAuthenticator();

    try {
      var authSession = globalClient.getAuthentication().beginAuthSessionViaCredentials(authDetails);
      AuthPollResult pollResponse = authSession.pollingWaitForResultCompat().get();

      if (pollResponse.getNewGuardData() != null) {
        loggedUser.setPreviouslyStoredGuardData(pollResponse.getNewGuardData());
        storeAuthToken(pollResponse.getRefreshToken());
      }

      logOnWithToken(pollResponse.getRefreshToken());
    } catch (Exception e) {
      handleAuthenticationException(e);
    }
  }

  public void defaultAuthentication(final LoginRequest request) {
    var newClient = generateDefaultClient();
    var newUserHandler = newClient.getHandler(SteamUser.class);
    LogOnDetails details = new LogOnDetails();
    details.setUsername(request.getUsername());
    details.setPassword(request.getPassword());
    newUserHandler.logOn(details);
  }

  private void handleAuthenticationException(Exception e) {
    if (e instanceof AuthenticationException) {
      log.error("An Authentication error has occurred. " + e.getMessage());
    } else if (e instanceof CancellationException) {
      log.error("An Cancellation exception was raised. Usually means a timeout occurred. " + e.getMessage());
    } else {
      log.error("An error occurred:" + e.getMessage());
    }

    systemSteamUser.logOff();
  }

  public String getAuthToken() {
    return (String) cacheService.get(steamSecrets.getAuthTokenCacheKey());
  }

  public void storeAuthToken(String token) {
    cacheService.set(steamSecrets.getAuthTokenCacheKey(), token);
  }

  public void connectClient() {
    log.info("Connecting to steam client...");
    globalClient.connect();
  }

  public void disconnectClient() {
    globalClient.disconnect();
  }
}
