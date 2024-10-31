package com.steam_analyser.analytics.application.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.steam_analyser.analytics.application.services.abstractions.ICacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService implements ICacheService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }
}
