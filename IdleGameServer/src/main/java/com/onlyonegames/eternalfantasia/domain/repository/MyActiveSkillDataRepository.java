package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyActiveSkillData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyActiveSkillDataRepository extends JpaRepository<MyActiveSkillData, Long> {
    Optional<MyActiveSkillData> findByUseridUser(Long userId);
}
