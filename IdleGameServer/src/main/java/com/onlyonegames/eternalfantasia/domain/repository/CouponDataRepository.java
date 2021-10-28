package com.onlyonegames.eternalfantasia.domain.repository;

import com.onlyonegames.eternalfantasia.domain.model.entity.CouponData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponDataRepository extends JpaRepository<CouponData, Long> {
    Optional<CouponData> findByCode(String code);
}
