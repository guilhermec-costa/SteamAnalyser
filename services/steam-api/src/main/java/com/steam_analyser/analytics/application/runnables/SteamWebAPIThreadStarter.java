package com.steam_analyser.analytics.application.runnables;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.schedulers.ISteamChron;
import com.steam_analyser.analytics.application.services.GlobalCallbackDispatcher;
import com.steam_analyser.analytics.application.services.SteamConfigManager;
import com.steam_analyser.analytics.application.services.SteamWebAPIProcessor;

import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SteamWebApiThreadStarter implements ApplicationRunner {

  private final SteamWebAPIProcessor steamWebAPIService;
  private final SteamConfigManager steamAuthenticator;
  private final GlobalCallbackDispatcher globalCallbackDispatcher;
  private final List<ISteamChron> steamChrons;
  private Thread webApiThread;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LogManager.addListener(new DefaultLogListener());
    webApiThread = new Thread(new SteamWebApiConnector(
        steamChrons,
        steamWebAPIService,
        steamAuthenticator,
        globalCallbackDispatcher));
    webApiThread.start();
  }

}
