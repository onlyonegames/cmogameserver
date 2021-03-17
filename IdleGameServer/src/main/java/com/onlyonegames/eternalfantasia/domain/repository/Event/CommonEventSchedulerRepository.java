package com.onlyonegames.eternalfantasia.domain.repository.Event;

import com.onlyonegames.eternalfantasia.domain.model.entity.Event.CommonEventScheduler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CommonEventSchedulerRepository extends JpaRepository<CommonEventScheduler, Long> {
    List<CommonEventScheduler> findByEventContentsTable_idAndStartTimeBeforeAndEndTimeAfter(Integer typeId, LocalDateTime after, LocalDateTime before);//TODO EventContentsTable_id 제거 및 이용 Service 코드 수정 필요
    List<CommonEventScheduler> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime after, LocalDateTime before);
    List<CommonEventScheduler> findByEventContentsTable_idOrderByEndTimeDesc(Integer typeId);
}