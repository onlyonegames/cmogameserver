package com.onlyonegames.eternalfantasia.domain.repository.Iap;

import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.IapTransactionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IapTransactionDataRepository extends JpaRepository<IapTransactionData, Long> {

    Optional<IapTransactionData> findByGamepotOrderId(String orderId);
    List<IapTransactionData> findByUseridUserAndState(Long userId, int state);
}
