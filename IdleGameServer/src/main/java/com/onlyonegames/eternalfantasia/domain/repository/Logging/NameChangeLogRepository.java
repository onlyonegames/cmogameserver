package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.NameChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NameChangeLogRepository extends JpaRepository<NameChangeLog, Long> {
    List<NameChangeLog> findAllByUseridUser(Long userId);
}
