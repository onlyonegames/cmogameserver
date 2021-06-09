package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPixieInfoData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPixieInfoDataRepository extends JpaRepository<MyPixieInfoData, Long> {
    Optional<MyPixieInfoData> findByUseridUser(Long userId);
}
