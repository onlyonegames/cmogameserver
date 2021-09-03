package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyChatBlockInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyChatBlockInfoRepository extends JpaRepository<MyChatBlockInfo, Long> {
    Optional<MyChatBlockInfo> findByUseridUser(Long userId);
}
