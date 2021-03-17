package com.onlyonegames.eternalfantasia.domain.repository.Iap;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.EternalPasses;
import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.SeasonLimitPackageSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeasonLimitPackageScheduleRepository extends JpaRepository<SeasonLimitPackageSchedule, Long> {
    List<SeasonLimitPackageSchedule> findAllBySeasonStartTimeBeforeAndSeasonEndTimeAfter(LocalDateTime startTime, LocalDateTime endTime);
}
