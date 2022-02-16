package com.onlyonegames.eternalfantasia.domain.model.dto.AppleIap;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class UserReceipt {

    // TODO - User(구매자) 정보에 대한 Fields 필요.

    @JsonAlias("receipt-data")
    private String receiptData;
}
