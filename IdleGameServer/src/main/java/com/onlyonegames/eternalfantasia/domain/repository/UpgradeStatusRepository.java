package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.UpgradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpgradeStatusRepository  extends JpaRepository<UpgradeStatus, Long>
{
    Optional<UpgradeStatus> findByUseridUser(Long userId);
}