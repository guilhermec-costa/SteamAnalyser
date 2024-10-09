package com.steam_analyser.analytics.api.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("user/stats")
public class UserStatsController {
  
  @GetMapping
  public String numberOfCurrentPlayersByApp(@RequestParam Integer appId) {
    System.out.println(appId);
    return "hello world";
  }
}
