package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyCollectionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyCollectionInfoRepository extends JpaRepository<MyCollectionInfo, Long> {
    Optional<MyCollectionInfo> findByUseridUser (Long userId);
}
