package com.onlyonegames.eternalfantasia.domain.repository.Iap;

import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.GooglePurchaseData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GooglePurchaseDataRepository extends JpaRepository<GooglePurchaseData, Long> {
    List<GooglePurchaseData> findAllByUseridUser(Long userId);
    Optional<GooglePurchaseData> findByOrderId(String orderId);
}
