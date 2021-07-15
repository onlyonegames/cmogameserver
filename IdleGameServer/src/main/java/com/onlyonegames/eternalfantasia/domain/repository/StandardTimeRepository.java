package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.StandardTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandardTimeRepository extends JpaRepository<StandardTime, Integer> {
}
