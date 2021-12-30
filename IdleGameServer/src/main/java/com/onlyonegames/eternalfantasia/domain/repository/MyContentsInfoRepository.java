package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyContentsInfoRepository extends JpaRepository<MyContentsInfo, Long> {
    Optional<MyContentsInfo> findByUseridUser (Long userId);
}
