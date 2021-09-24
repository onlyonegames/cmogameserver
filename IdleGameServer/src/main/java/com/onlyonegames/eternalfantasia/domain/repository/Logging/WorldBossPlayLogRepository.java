package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.WorldBossPlayLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorldBossPlayLogRepository extends JpaRepository<WorldBossPlayLog, Long> {
    List<WorldBossPlayLog> findAllByUseridUser(Long userId);
}
