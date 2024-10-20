package com.steam_analyser.analytics.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.slf4j.*;

@Configuration
public class LoggerConfig {
 
  @Bean
  @Scope("prototype")
  Logger logger() {
    return LoggerFactory.getLogger(getClass());
  }
}
