package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CommonLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonLogRepository extends JpaRepository<CommonLog, Long> {
}
