package com.onlyonegames.eternalfantasia.domain.repository.Event;

import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyAchieveEventMissionsDataRepository extends JpaRepository<MyAchieveEventMissionsData, Long> {
    Optional<MyAchieveEventMissionsData> findByUseridUser(Long userid);
}
