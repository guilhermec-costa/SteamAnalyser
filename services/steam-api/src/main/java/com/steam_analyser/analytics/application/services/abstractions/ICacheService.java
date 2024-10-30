package com.steam_analyser.analytics.application.services.abstractions;

public interface ICacheService {
  
  void set(String key, Object value);
  Object get(String key);
}
