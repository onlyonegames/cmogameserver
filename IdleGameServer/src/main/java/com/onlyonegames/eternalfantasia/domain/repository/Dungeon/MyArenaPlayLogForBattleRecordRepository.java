package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayLogForBattleRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MyArenaPlayLogForBattleRecordRepository extends JpaRepository<MyArenaPlayLogForBattleRecord, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<MyArenaPlayLogForBattleRecord> findAllByUseridUser(Long userId, Sort sort);
}
