package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyRuneLevelInfoData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyRuneLevelInfoDataRepository extends JpaRepository<MyRuneLevelInfoData, Long> {
    Optional<MyRuneLevelInfoData> findByUseridUser(Long userId);
}
