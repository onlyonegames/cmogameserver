package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPassDataRepository extends JpaRepository<MyPassData, Long> {
    Optional<MyPassData> findByUseridUser(Long userId);
}
