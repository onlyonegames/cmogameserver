package com.onlyonegames.eternalfantasia.domain.repository.Event;

import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyMonsterKillEventMissionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyMonsterKillEventMissionDataRepository extends JpaRepository<MyMonsterKillEventMissionData, Long> {
    Optional<MyMonsterKillEventMissionData> findByUseridUser(Long userId);
}
