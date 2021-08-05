package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyGachaInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyGachaInfoRepository extends JpaRepository<MyGachaInfo, Long> {
    Optional<MyGachaInfo> findByUseridUser(Long userId);
}
