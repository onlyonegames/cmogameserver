package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyMissionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyMissionInfoRepository extends JpaRepository<MyMissionInfo, Long> {
    Optional<MyMissionInfo> findByUseridUser(Long userId);
}
