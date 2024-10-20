package com.steam_analyser.analytics.application.handlers;

import com.steam_analyser.analytics.application.events.ApplicationEvent;

public interface Handler {
  
  String getEventName();
  void handle(ApplicationEvent event);
}
