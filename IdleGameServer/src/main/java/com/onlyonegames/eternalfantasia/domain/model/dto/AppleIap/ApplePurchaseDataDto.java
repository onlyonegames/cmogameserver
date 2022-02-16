package com.onlyonegames.eternalfantasia.domain.model.dto.AppleIap;

import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.ApplePurchaseData;
import lombok.Data;

@Data
public class ApplePurchaseDataDto {
    Long id;
    Long useridUser;
    String productId;
    String transactionId;
    String originalTransactionId;
    String purchaseDateMs;
    String expirationDate;
    boolean consume;

    public void SetApplePurchaseDataDto (Long userId, InApp inApp) {
        this.useridUser = userId;
        this.productId = inApp.getProductId();
        this.transactionId = inApp.getOriginalTransactionId();
        this.purchaseDateMs = inApp.getPurchaseDateMs();
        this.expirationDate = inApp.getPurchaseDateMs();
        this.consume = false;
    }

    public ApplePurchaseData ToEntity() {
        return ApplePurchaseData.builder().useridUser(useridUser).productId(productId).transactionId(transactionId)
                .originalTransactionId(originalTransactionId).purchaseDateMs(purchaseDateMs).expirationDate(expirationDate).consume(consume).build();
    }
}
