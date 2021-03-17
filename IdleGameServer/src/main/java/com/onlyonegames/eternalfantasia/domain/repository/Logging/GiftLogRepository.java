package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.GiftLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftLogRepository extends JpaRepository<GiftLog, Long> {
    List<GiftLog> findAllByUseridUser(Long userId);
}
