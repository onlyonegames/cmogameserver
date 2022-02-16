package com.onlyonegames.eternalfantasia.domain.repository.Iap;

import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.ApplePurchaseData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplePurchaseDataRepository extends JpaRepository<ApplePurchaseData, Long> {
    Optional<ApplePurchaseData> findByTransactionId(String transactionId);
    List<ApplePurchaseData> findAllByUseridUser(Long userId);
}
