package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CharacterExpLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CharacterExpLogRepository extends JpaRepository<CharacterExpLog, Long> {
    List<CharacterExpLog> findAllByUseridUser(Long userId);
}
