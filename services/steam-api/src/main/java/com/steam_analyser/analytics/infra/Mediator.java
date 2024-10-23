package com.steam_analyser.analytics.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

import com.steam_analyser.analytics.application.events.ApplicationEvent;
import com.steam_analyser.analytics.application.handlers.Handler;

@Component
public class Mediator {
  
  private List<Handler<? extends ApplicationEvent>> handlers;

  public Mediator() {
    handlers = new ArrayList<>();
  }

  public void register(Handler<? extends ApplicationEvent> handler) {
    handlers.add(handler);
  }

  public void publish(ApplicationEvent event) {
    for(Handler<? extends ApplicationEvent> handler : handlers) {
      if(handler.getEventName().equals(event.name())) {

        @SuppressWarnings("unchecked")
        Handler<ApplicationEvent> typedHandler = (Handler<ApplicationEvent>) handler;
        typedHandler.handle(event);
      }
    }
  }
}
