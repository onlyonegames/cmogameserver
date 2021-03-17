package com.onlyonegames.eternalfantasia.domain.repository.Gotcha;

import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.PickupTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PickupTableRepository extends JpaRepository<PickupTable, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<PickupTable> findAllByStartDateAfterAndEndDateBefore(LocalDateTime startDate, LocalDateTime endDate);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<PickupTable> findAllByStartDateBeforeAndEndDateAfter(LocalDateTime startAfterDate, LocalDateTime endBeforeDate);
}

