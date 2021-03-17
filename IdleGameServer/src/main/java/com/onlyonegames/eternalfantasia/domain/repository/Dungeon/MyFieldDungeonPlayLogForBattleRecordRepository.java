package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyFieldDungeonPlayLogForBattleRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFieldDungeonPlayLogForBattleRecordRepository extends JpaRepository<MyFieldDungeonPlayLogForBattleRecord, Long> {
    List<MyFieldDungeonPlayLogForBattleRecord> findAllByUseridUser(Long userId, Sort sort);
}
