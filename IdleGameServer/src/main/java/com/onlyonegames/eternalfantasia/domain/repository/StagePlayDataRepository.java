package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.StagePlayData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface StagePlayDataRepository extends JpaRepository<StagePlayData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    StagePlayData findByUseridUser(Long userId);
}
