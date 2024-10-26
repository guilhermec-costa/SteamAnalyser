package com.steam_analyser.analytics.util;

import java.util.concurrent.Executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadUtil {

  private static int threadScaleFactor = 1;

  static public int getProcessorsNumber() {
    return Runtime.getRuntime().availableProcessors();
  }

  static public Executor getTaskExecutor(Integer nThread, String prefix) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    var threadNumber = nThread != null ? nThread : (getProcessorsNumber() * threadScaleFactor);
    executor.setCorePoolSize(threadNumber);
    executor.setMaxPoolSize(threadNumber + 5);
    executor.setQueueCapacity(threadNumber);

    if(prefix != null)
      executor.setThreadNamePrefix(prefix);

    executor.initialize();
    return executor;
  }
}
