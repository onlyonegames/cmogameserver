package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyDungeonInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyDungeonInfoRepository extends JpaRepository<MyDungeonInfo, Long> {
    Optional<MyDungeonInfo> findByUseridUser(Long userId);
}
