package com.onlyonegames.eternalfantasia.domain.repository.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerStagePlayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyHeroTowerStagePlayDataRepository extends JpaRepository<MyHeroTowerStagePlayData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyHeroTowerStagePlayData findByUseridUser(Long userId);
}
