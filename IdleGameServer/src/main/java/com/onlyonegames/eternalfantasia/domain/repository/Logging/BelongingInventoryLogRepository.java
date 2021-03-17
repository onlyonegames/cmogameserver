package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.BelongingInventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BelongingInventoryLogRepository extends JpaRepository<BelongingInventoryLog, Long> {
    List<BelongingInventoryLog> findAllByUseridUser(Long userId);
}
