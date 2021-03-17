package com.onlyonegames.eternalfantasia.domain.repository.EternalPass;

import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.EternalPasses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EternalPassesRepository extends JpaRepository<EternalPasses, Integer> {
    List<EternalPasses> findByPassStartTimeBeforeAndPassEndTimeAfter(LocalDateTime startTime, LocalDateTime endTime);
}
