package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyStatusInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyStatusInfoRepository extends JpaRepository<MyStatusInfo, Long>
{
    Optional<MyStatusInfo> findByUseridUser(Long userId);
}