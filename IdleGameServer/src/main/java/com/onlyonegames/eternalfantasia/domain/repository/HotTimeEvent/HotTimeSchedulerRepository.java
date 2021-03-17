package com.onlyonegames.eternalfantasia.domain.repository.HotTimeEvent;

import com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent.HotTimeScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface HotTimeSchedulerRepository extends JpaRepository<HotTimeScheduler,Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<HotTimeScheduler> findByStartTimeBeforeAndEndTimeAfterAndKind(LocalDateTime after, LocalDateTime before, int kind);
}
