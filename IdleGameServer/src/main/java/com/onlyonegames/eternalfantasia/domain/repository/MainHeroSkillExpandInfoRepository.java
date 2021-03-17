package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MainHeroSkillExpandInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MainHeroSkillExpandInfoRepository extends JpaRepository<MainHeroSkillExpandInfo, Integer> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    List<MainHeroSkillExpandInfo> findAllByExclusiveType(String exclusiveType);
}