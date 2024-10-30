package com.steam_analyser.analytics.application.services.abstractions;

public interface ICacheService {

  public void set(String key, Object value);

  public Object get(String key);
}
