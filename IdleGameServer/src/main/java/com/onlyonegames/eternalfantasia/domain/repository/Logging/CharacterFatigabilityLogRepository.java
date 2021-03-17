package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CharacterFatigabilityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterFatigabilityLogRepository extends JpaRepository<CharacterFatigabilityLog, Long> {
    List<CharacterFatigabilityLog> findAllByUseridUser(Long userId);
}
