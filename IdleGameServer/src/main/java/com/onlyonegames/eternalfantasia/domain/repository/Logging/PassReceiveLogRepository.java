package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.PassReceiveLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassReceiveLogRepository extends JpaRepository<PassReceiveLog, Long> {
    List<PassReceiveLog> findAllByUseridUser(Long userId);
}
