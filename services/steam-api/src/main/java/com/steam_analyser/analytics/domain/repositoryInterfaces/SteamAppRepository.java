package com.steam_analyser.analytics.domain.repositoryInterfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.domain.entities.SteamApp;

@Repository
public interface SteamAppRepository extends JpaRepository<SteamApp, Long> {
}
