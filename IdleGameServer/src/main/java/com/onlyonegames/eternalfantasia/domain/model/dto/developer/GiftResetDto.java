package com.onlyonegames.eternalfantasia.domain.model.dto.developer;

import lombok.Data;

import java.util.List;

@Data
public class GiftResetDto {
    Long id;
    int resetCount;
    List<String> resetGiftCodeList;
    String heroCode;

}
