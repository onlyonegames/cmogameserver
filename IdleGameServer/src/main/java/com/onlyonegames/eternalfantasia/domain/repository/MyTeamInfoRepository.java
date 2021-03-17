package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MyTeamInfoRepository extends JpaRepository<MyTeamInfo, Long>, JpaSpecificationExecutor<MyTeamInfo> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    MyTeamInfo findByUseridUser(Long userId);
    List<MyTeamInfo> findByUseridUserIn(List<Long> useridList);
}
