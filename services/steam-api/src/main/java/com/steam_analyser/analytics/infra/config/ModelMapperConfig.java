package com.steam_analyser.analytics.infra.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
  
  @Bean
  ModelMapper modellMapper() {
    ModelMapper modelMapper = new ModelMapper();
    return modelMapper;
  }
}
