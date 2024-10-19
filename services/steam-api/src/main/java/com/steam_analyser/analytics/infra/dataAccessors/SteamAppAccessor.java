package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.models.SteamAppModel;

@Repository
public interface SteamAppAccessor extends JpaRepository<SteamAppModel, Long> {
}
