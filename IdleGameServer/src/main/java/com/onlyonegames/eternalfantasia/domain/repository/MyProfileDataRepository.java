package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyProfileDataRepository extends JpaRepository<MyProfileData, Long> {
    Optional<MyProfileData> findByUseridUser(Long userId);
}
