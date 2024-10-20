package com.steam_analyser.analytics.application.runnables;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.chrons.ISteamChron;
import com.steam_analyser.analytics.infra.config.SteamSecretsProperties;

import in.dragonbra.javasteam.util.log.DefaultLogListener;
import in.dragonbra.javasteam.util.log.LogManager;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SteamWebAPIThreadStarter implements ApplicationRunner {

  private final SteamSecretsProperties steamSecretsProperties;
  private final List<ISteamChron> steamChrons;
  private Thread webApiThread;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LogManager.addListener(new DefaultLogListener());
    webApiThread = new Thread(new SteamWebApiRunnable(steamSecretsProperties, steamChrons));
    webApiThread.start();
  }

}
