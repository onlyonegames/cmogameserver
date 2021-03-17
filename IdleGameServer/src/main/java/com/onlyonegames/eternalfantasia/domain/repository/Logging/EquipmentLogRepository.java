package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.EquipmentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentLogRepository extends JpaRepository<EquipmentLog, Long> {
    List<EquipmentLog> findAllByUseridUser(Long userId);
}
