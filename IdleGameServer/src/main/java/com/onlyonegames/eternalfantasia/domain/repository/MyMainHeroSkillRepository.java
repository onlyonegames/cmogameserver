package com.onlyonegames.eternalfantasia.domain.repository;

import java.util.List;
import java.util.Optional;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface MyMainHeroSkillRepository extends JpaRepository<MyMainHeroSkill, Long> {
    // Optional<MainHeroSkill> findBySkillid(int skillid);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<MyMainHeroSkill> findAllByUseridUser(Long userId);
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyMainHeroSkill> findByUseridUserAndBaseSkillid(Long userId, int baseSkillid);
}