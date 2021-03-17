package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CurrencyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyLogRepository extends JpaRepository<CurrencyLog, Long> {
    List<CurrencyLog> findAllByUseridUser(Long userId);
}
