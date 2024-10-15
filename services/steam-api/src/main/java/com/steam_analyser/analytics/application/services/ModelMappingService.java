package com.steam_analyser.analytics.application.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ModelMappingService {
 
  private final ModelMapper modelMapper;

  public <S, D> List<D> mapList(List<S> sourceItems, Class<D> theClass) {
    return sourceItems.stream()
      .map(element -> modelMapper.map(element, theClass))
      .collect(Collectors.toList());
  }
}
