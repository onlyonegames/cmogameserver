package com.onlyonegames.eternalfantasia.domain.repository.Companion;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionStarPointsAverageTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CompanionStarPointsAverageRepository extends JpaRepository<CompanionStarPointsAverageTable, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<CompanionStarPointsAverageTable> findByUseridUser(Long userId);
}
