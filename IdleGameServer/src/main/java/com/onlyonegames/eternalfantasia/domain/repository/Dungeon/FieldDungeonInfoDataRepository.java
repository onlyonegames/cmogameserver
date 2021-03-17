package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.FieldDungeonInfoData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldDungeonInfoDataRepository extends JpaRepository<FieldDungeonInfoData, Integer> {
    Optional<FieldDungeonInfoData> findByNowSeasonNo(int seasonNo);
}
