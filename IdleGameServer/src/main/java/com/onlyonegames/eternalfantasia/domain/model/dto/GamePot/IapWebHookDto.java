package com.onlyonegames.eternalfantasia.domain.model.dto.GamePot;

//userId={uuid}&
// orderId={orderId}&
// projectId={projectId}&
// platform={platform}&
// productId={productId}&
// store={store}&
// payment={payment}&
// transactionId={transactionId}&
// gamepotOrderId={gamepotOrderId}&
// uniqueId={uniqueId}

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IapWebHookDto {
    public String userId;
    public String transactionId;
    public String store;
    public String projectId;
    public String productId;
    public String platform;
    public String payment;
    public String uniqueId;
    public String gamepotOrderId;
    public String serverId;
    public String playerId;
    public String etc;
}

