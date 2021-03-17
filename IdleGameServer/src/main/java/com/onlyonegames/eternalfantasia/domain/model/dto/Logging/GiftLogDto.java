package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import lombok.Data;

@Data
public class GiftLogDto {
    String workingPosition;
    String code;
    int previousValue;
    int changeNum;
    int presentValue;

    public void setGiftLogDto(String workingPosition, String code, int changeNum, int presentValue) {
        this.workingPosition = workingPosition;
        this.code = code;
        this.changeNum = changeNum;
        this.presentValue = presentValue;
    }
}
