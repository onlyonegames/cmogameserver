package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonStagePlayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyAncientDragonStagePlayDataRepository extends JpaRepository<MyAncientDragonStagePlayData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyAncientDragonStagePlayData findByUseridUser(Long userId);
}
