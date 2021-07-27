package com.onlyonegames.eternalfantasia.domain.repository.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyWorldBossPlayDataRepository extends JpaRepository<MyWorldBossPlayData, Long> {
    Optional<MyWorldBossPlayData> findByUseridUser(Long userId);
}
