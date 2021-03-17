package com.onlyonegames.eternalfantasia.domain.model.dto.Iap;

import com.onlyonegames.eternalfantasia.domain.model.entity.Iap.IapTransactionData;
import lombok.Data;

import javax.persistence.Column;

@Data
public class IapTransactionDataDto {
    Long id;
    Long useridUser;
    String transactionId;
    String store;
    String projectId;
    String productId;
    String platform;
    String payment;
    String gamepotOrderId;
    int state;
    public IapTransactionData ToEntity() {
        return IapTransactionData.builder().useridUser(useridUser).transactionId(transactionId).store(store).projectId(projectId).productId(productId).platform(platform).payment(payment).state(state).build();
    }
}
