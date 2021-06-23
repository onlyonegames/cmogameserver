package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassiveSkillData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPassiveSkillDataRepository extends JpaRepository<MyPassiveSkillData, Long> {
    Optional<MyPassiveSkillData> findByUseridUser(Long userId);
}
