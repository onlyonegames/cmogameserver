package com.onlyonegames.eternalfantasia.domain.repository.Cupon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Coupon.CouponInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuponInfoRepository extends JpaRepository<CouponInfo, Long> {
    Optional<CouponInfo> findByCouponNo(String cuponNo);
    Optional<List<CouponInfo>> findAllByUseridUser(Long useridUser);
}
