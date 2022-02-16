package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class AppleStoreResponseDto {
    @JsonAlias("Store")
    String store;
    @JsonAlias("TransactionID")
    String transactionID;
    @JsonAlias("Payload")
    String payload;

    @Data
    public static class ReceiptData{
        String environment;
        int status;
        Receipt receipt;
    }

    @Data
    public static class Receipt {
        Long adam_id;
        Long app_item_id;
        String application_version;
        String bundle_id;
        int download_id;
        String expiration_data;
        String expiration_date_ms;
        String expiration_date_pst;
        In_AppData in_app;
        String original_application_version;
        String original_purchase_date;
        String original_purchase_date_ms;
        String original_purchase_date_pst;
        String preorder_date;
        String preorder_date_ms;
        String preorder_date_pst;
        String receipt_creation_date;
        String receipt_creation_date_ms;
        String receipt_creation_date_pst;
        String receipt_type;
        String request_date;
        String request_date_ms;
        String request_date_pst;
        int version_external_identifier;
    }

    @Data
    public static class In_AppData {
        String cancellation_date;
        String cancellation_date_ms;
        String cancellation_date_pst;
        String cancellation_reason;
        String expires_date;
        String expires_date_ms;
        String expires_date_pst;
        String is_in_intro_offer_period;
        String is_trial_period;
        String original_purchase_date;
        String original_purchase_date_ms;
        String original_purchase_date_pst;
        String original_transaction_id;
        String product_id;
        String promotional_offer_id;
        String purchase_date;
        String purchase_date_ms;
        String purchase_date_pst;
        String quantity;
        String transaction_id;
        String web_order_line_item_id;
    }
}