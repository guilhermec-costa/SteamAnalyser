package com.steam_analyser.analytics.application.chrons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.steam_analyser.analytics.application.events.PlayerCountUpdatedEvent;
import com.steam_analyser.analytics.application.events.datatypes.PlayerCountUpdatedArgument;
import com.steam_analyser.analytics.application.services.SteamAppService;
import com.steam_analyser.analytics.application.services.SteamAppStatsService;
import com.steam_analyser.analytics.infra.Mediator;
import com.steam_analyser.analytics.models.SteamAppModel;
import com.steam_analyser.analytics.models.SteamAppStatsModel;

import in.dragonbra.javasteam.steam.steamclient.configuration.SteamConfiguration;
import in.dragonbra.javasteam.types.KeyValue;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.lang.Math;

import static com.steam_analyser.analytics.api.SteamRouteInterfaces.*;
import static com.steam_analyser.analytics.api.SteamRouteMethods.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class UpdateCurrentPlayersChron implements ISteamChron {

  private final SteamAppService steamAppService;
  private final SteamAppStatsService steamAppStatsService;
  private final Mediator mediator;
  private final TaskScheduler taskScheduler;
  private SteamConfiguration steamConfiguration;
  private final int executionFrequency = 900_000;

  @SuppressWarnings("deprecation")
  public void start(final SteamConfiguration theSteamConfiguration) {
    steamConfiguration = theSteamConfiguration;
    taskScheduler.scheduleAtFixedRate(this::run, executionFrequency);
    log.info("Executing task: \"" + getChronName() + "\"");
  }

  @Override
  public void run() {
    var steamAppsList = steamAppService.findAllSteamApps();
    var batches = partitionList(steamAppsList, processBatchSizeFor(steamAppsList));
    List<CompletableFuture<Void>> futures = batches.stream()
        .map(this::processBatchParallely)
        .collect(Collectors.toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  private CompletableFuture<Void> processBatchParallely(List<SteamAppModel> batch) {
    ExecutorService executor = Executors.newFixedThreadPool(3);

    return CompletableFuture.runAsync(() -> {
      List<PlayerCountUpdatedArgument> toBePropagated = new ArrayList<>();
      List<SteamAppStatsModel> statsToSave = new ArrayList<>();

      for (var nextApp : batch) {
        SteamAppStatsModel actUponAppStats = steamAppStatsService.findOrCreateStatsModelInstance(nextApp);
        Integer playerCount = queryPlayerCountForApp(nextApp.getSteamAppId());
        actUponAppStats.updateCurrentPlayers(playerCount);
        statsToSave.add(actUponAppStats);
        toBePropagated.add(new PlayerCountUpdatedArgument(nextApp, playerCount, getExecutionDate()));
      }

      steamAppStatsService.saveMultiple(statsToSave);
      propagateSideEffects(toBePropagated);
    }, executor);
  }

  private Integer extractPlayerCountingFromResponse(KeyValue res) {
    String textValue = res.getChildren().getFirst().getValue();
    return Integer.valueOf(textValue);
  }

  @Override
  public String getChronName() {
    return getClass().getName();
  }

  private Integer queryPlayerCountForApp(String steamAppId) {
    try {
      Map<String, String> args = new HashMap<>();
      args.put("appid", steamAppId);

      var currentPlayersResponse = steamConfiguration.getWebAPI(ISteamUserStats.string)
          .call(
              GetNumberOfCurrentPlayers.string,
              GetNumberOfCurrentPlayers.version, args);

      return extractPlayerCountingFromResponse(currentPlayersResponse);
    } catch (IOException e) {
      // log.error(e.getLocalizedMessage());
      System.out.println(e.getCause());
      return null;
    }
  }

  private void propagateSideEffects(List<PlayerCountUpdatedArgument> eventArgs) {
    PlayerCountUpdatedEvent updateCountEvent = new PlayerCountUpdatedEvent(eventArgs);
    mediator.publish(updateCountEvent);
  }

  private <T> int processBatchSizeFor(List<T> items) {
    return 500;
  }

  private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
    var partionContainer = new ArrayList<List<T>>();
    int itemsCount = list.size();

    for (int i = 0; i < itemsCount; i += batchSize) {
      int nextPartionLimit = Math.min(i + batchSize, itemsCount);
      var singlePartion = list.subList(i, nextPartionLimit);
      partionContainer.add(singlePartion);
    }
    return partionContainer;
  }
}
