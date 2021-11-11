package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.GetSetLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GetSetLogRepository extends JpaRepository<GetSetLog, Long> {
    List<GetSetLog> findAllByUseridUser(Long userId);
}
