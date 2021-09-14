package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

@Data
public class IapResponseDto {
    String store;
    String transactionID;
    String payload;

    @Data
    public static class PayLoad{
        String json;
        String signature;
        String skuDetails;
    }
    @Data
    public static class SignedData{
        String orderId;
        String packageName;
        String productId;
        Long purchaseTime;
        int purchaseState;
        String purchaseToken;
        boolean acknowledged;
    }
    @Data
    public static class SkuDetails{
        String orderId;
        String type;
        String title;
        String name;
        String price;
        Long price_amount_micros;
        String price_currency_code;
        String description;
        String skuDetailsToken;
    }
}
