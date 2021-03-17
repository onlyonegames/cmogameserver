package com.onlyonegames.eternalfantasia.domain.repository.Mission;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyMissionsDataRepository extends JpaRepository<MyMissionsData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyMissionsData> findByUseridUser(Long userId);
}
