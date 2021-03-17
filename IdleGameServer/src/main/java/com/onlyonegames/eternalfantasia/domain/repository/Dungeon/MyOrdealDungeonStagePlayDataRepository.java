package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyOrdealDungeonStagePlayDataRepository extends JpaRepository<MyOrdealDungeonStagePlayData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyOrdealDungeonStagePlayData findByUseridUser(Long userId);
}
