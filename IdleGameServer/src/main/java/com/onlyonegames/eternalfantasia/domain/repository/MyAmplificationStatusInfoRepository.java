package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyAmplificationStatusInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyAmplificationStatusInfoRepository extends JpaRepository<MyAmplificationStatusInfo, Long> {
    Optional<MyAmplificationStatusInfo> findByUseridUser(Long userId);
}
