package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

@Data
public class CurrencyLogDto {
    String workingPosition;
    String currencyType;
    int previousValue;
    int changeNum;
    int presentValue;

    public void setCurrencyLogDto(String workingPosition, String currencyType, int previousValue, int changeNum, int presentValue) {
        this.workingPosition = workingPosition;
        this.currencyType = currencyType;
        this.previousValue = previousValue;
        this.changeNum = changeNum;
        this.presentValue = presentValue;
    }
}
