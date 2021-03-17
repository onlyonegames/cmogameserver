package com.onlyonegames.eternalfantasia.domain.repository;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyFieldSaveData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyFieldSaveDataRepository extends JpaRepository<MyFieldSaveData, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyFieldSaveData> findByUseridUser(Long userId);
}
