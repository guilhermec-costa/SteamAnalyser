package com.steam_analyser.analytics.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

import com.steam_analyser.analytics.application.events.ApplicationEvent;
import com.steam_analyser.analytics.application.handlers.Handler;

@Component
public class Mediator {
  
  private List<Handler> handlers;

  public Mediator() {
    handlers = new ArrayList<>();
  }

  public void register(Handler handler) {
    handlers.add(handler);
  }

  public void publish(ApplicationEvent event) {
    for(var handler : handlers) {
      if(handler.getEventName().equals(event.name())) {
        handler.handle(event);
      }
    }
  }
}
