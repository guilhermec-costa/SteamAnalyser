package com.steam_analyser.analytics.application.runnables;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.steam_analyser.analytics.application.handlers.Handler;
import com.steam_analyser.analytics.infra.Mediator;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EventHandlersInitializer implements ApplicationRunner {

  private final Mediator mediator;
  private final List<Handler<?>> registrableHandlers;

  @Override
  public void run(ApplicationArguments args) {
    for (Handler<?> h : registrableHandlers) {
      mediator.register(h);
    }
  }
}
