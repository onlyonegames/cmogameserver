package com.onlyonegames.eternalfantasia.domain.repository.Companion;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyLinkweaponInfoRepository extends JpaRepository<MyLinkweaponInfo, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyLinkweaponInfo> findByUseridUser(Long userId);
}
