package com.onlyonegames.eternalfantasia.domain.repository.Companion;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.CompanionUserEvaluationTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanionUserEvaluationRepository extends JpaRepository<CompanionUserEvaluationTable, String> {
}
