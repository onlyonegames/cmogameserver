package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyBoosterInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyBoosterInfoRepository extends JpaRepository<MyBoosterInfo, Long> {
    Optional<MyBoosterInfo> findByUseridUser(Long userId);
}
