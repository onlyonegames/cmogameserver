package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyShopInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyShopInfoRepository extends JpaRepository<MyShopInfo, Long> {
    Optional<MyShopInfo> findByUseridUser(Long userId);
}
