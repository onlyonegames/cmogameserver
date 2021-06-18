package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyEquipmentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyEquipmentInfoRepository extends JpaRepository<MyEquipmentInfo, Long> {
    Optional<MyEquipmentInfo> findByUseridUser(Long userId);
}
