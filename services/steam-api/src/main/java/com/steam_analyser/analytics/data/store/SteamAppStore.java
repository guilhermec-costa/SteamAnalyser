package com.steam_analyser.analytics.data.store;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.data.models.SteamAppModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface SteamAppStore extends JpaRepository<SteamAppModel, Long> {

  @Query("select sa from SteamAppModel sa order by sa.id")
  public List<SteamAppModel> findNElements(Pageable pageable);

  public Optional<SteamAppModel> findBySteamAppId(Integer steamAppId);
}
