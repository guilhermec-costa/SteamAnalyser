package com.steam_analyser.analytics.application.handlers;

import com.steam_analyser.analytics.application.events.ApplicationEvent;

public interface Handler<T extends ApplicationEvent> {
  
  String getEventName();
  void handle(T event);
}
