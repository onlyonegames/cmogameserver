package com.onlyonegames.eternalfantasia.domain.repository.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CouponUsedLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponUsedLogRepository extends JpaRepository<CouponUsedLog, Long> {
    Optional<CouponUsedLog> findByUsedCouponId (Long usedCouponId);
    Optional<CouponUsedLog> findByUseridUserAndUsedCouponId (Long userId, Long usedCouponId);
    List<CouponUsedLog> findAllByUseridUser (Long userId);
}
