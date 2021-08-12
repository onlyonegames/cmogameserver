package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyQuickMissionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyQuickMissionDataRepository extends JpaRepository<MyQuickMissionData, Long> {
    Optional<MyQuickMissionData> findByUseridUser(Long userId);
}
