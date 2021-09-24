package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.ShopPurchaseLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopPurchaseLogRepository extends JpaRepository<ShopPurchaseLog, Long> {
    List<ShopPurchaseLog> findAllByUseridUser(Long userId);
}
