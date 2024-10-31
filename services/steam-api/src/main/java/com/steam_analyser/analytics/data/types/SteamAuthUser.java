package com.steam_analyser.analytics.data.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamAuthUser {
  private String username;
  private String password;
  private String previouslyStoredGuardData;
}
