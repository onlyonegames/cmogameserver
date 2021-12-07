package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyEventExchangeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyEventExchangeInfoRepository extends JpaRepository<MyEventExchangeInfo, Long> {
    Optional<MyEventExchangeInfo> findByUseridUser(Long userId);
}
