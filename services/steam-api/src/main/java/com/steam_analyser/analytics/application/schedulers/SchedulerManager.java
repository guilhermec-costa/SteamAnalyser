package com.steam_analyser.analytics.application.schedulers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SchedulerManager implements ApplicationRunner {

  private Map<String, Map<ChronControllerFlags, Boolean>> chronControllers = new HashMap<>();
  private TaskScheduler taskScheduler;

  public SchedulerManager(final @Qualifier("sharedTaskScheduler") TaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  @Override
  public void run(ApplicationArguments args) {
    chronControllers.put(RegisterNewAppsChron.class.getName(),
        new HashMap<>(Map.of(ChronControllerFlags.SHOULD_RUN, false)));

    chronControllers.put(UpdatePlayersPriorityBasedAppsChron.class.getName(),
        new HashMap<>(Map.of(ChronControllerFlags.SHOULD_RUN, true)));

    chronControllers.put(UpdatePlayersForAllAppsChron.class.getName(),
        new HashMap<>(Map.of(ChronControllerFlags.SHOULD_RUN, false)));

  }

  public <T extends ISteamChron> void scheduleChronIfAllowed(T chron) {
    var chronController = chronControllers.get(chron.getChronName());
    if (chronController.get(ChronControllerFlags.SHOULD_RUN)) {
      taskScheduler.scheduleAtFixedRate(chron::run, chron.getExecutionFrequency());
      log.info("Executing chron: \"" + chron.getChronName() + "\"");
      return;
    }

    log.info("Skipping chron execution: \"" + chron.getChronName() + "\"");
  }

  private enum ChronControllerFlags {
    SHOULD_RUN
  }
}
