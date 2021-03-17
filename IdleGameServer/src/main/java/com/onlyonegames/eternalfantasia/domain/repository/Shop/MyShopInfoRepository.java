package com.onlyonegames.eternalfantasia.domain.repository.Shop;

import com.onlyonegames.eternalfantasia.domain.model.entity.Shop.MyShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MyShopInfoRepository extends JpaRepository<MyShopInfo, Long> {
    //@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    Optional<MyShopInfo> findByUseridUser(Long userId);
}
