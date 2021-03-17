package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerPlayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyInfiniteTowerPlayDataRepository extends JpaRepository<MyInfiniteTowerPlayData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyInfiniteTowerPlayData findByUseridUser(Long userId);
}
