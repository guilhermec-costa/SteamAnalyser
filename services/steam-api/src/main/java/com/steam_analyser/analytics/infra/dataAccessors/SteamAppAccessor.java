package com.steam_analyser.analytics.infra.dataAccessors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.steam_analyser.analytics.models.SteamAppModel;
import java.util.List;

@Repository
public interface SteamAppAccessor extends JpaRepository<SteamAppModel, Long> {

  @Query("select sa from SteamAppModel sa order by sa.id")
  public List<SteamAppModel> findNElements(Pageable pageable);
}
