package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonSeasonSaveData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyFieldDungeonSeasonSaveDataRepository extends JpaRepository<MyFieldDungeonSeasonSaveData, Long> {
    Optional<MyFieldDungeonSeasonSaveData> findByUseridUser(Long userId);
}
