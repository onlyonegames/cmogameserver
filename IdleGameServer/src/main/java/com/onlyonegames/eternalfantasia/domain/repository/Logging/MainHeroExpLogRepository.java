package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.MainHeroExpLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainHeroExpLogRepository extends JpaRepository<MainHeroExpLog, Long> {
    List<MainHeroExpLog> findAllByUseridUser(Long userId);
}
