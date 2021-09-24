package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.ArenaPlayLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArenaPlayLogRepository extends JpaRepository<ArenaPlayLog, Long> {
    List<ArenaPlayLog> findAllByUseridUser(Long userId);
}
