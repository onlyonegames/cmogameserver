package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyClassPotentialityData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyClassPotentialityDataRepository extends JpaRepository<MyClassPotentialityData, Long> {
    Optional<MyClassPotentialityData> findByUseridUser(Long userId);
}
