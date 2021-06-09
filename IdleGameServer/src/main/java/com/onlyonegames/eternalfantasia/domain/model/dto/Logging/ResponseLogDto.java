package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.BelongingInventoryLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CurrencyLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.EquipmentLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.GiftLog;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResponseLogDto {
    List<CurrencyResponseDto> currencyResponseDtoList;
    List<GiftResponseDto> giftResponseDtoList;
    public static class CurrencyResponseDto{
        public Long id;
        public LocalDateTime createdData;
        public String workingPosition;
        public String currencyType;
        public int previousValue;
        public int changeNum;
        public int presentValue;

        public void setCurrencyResponseDto(CurrencyLog currencyLog, CurrencyLogDto currencyLogDto) {
            this.id = currencyLog.getId();
            this.createdData = currencyLog.getCreateddate();
            this.workingPosition = currencyLogDto.getWorkingPosition();
            this.currencyType = currencyLogDto.getCurrencyType();
            this.previousValue = currencyLogDto.getPreviousValue();
            this.changeNum = currencyLogDto.getChangeNum();
            this.presentValue = currencyLogDto.getPresentValue();
        }
    }

    public static class GiftResponseDto{
        public Long id;
        public LocalDateTime createdData;
        public String workingPosition;
        public String name;
        public String code;
        public int previousValue;
        public int changeNum;
        public int presentValue;

        public void setGiftResponseDto(GiftLog giftLog, GiftLogDto giftLogDto, String name) {
            this.id = giftLog.getId();
            this.createdData = giftLog.getCreateddate();
            this.workingPosition = giftLogDto.getWorkingPosition();
            this.name = name;
            this.code = giftLogDto.getCode();
            this.previousValue = giftLogDto.getPreviousValue();
            this.changeNum = giftLogDto.getChangeNum();
            this.presentValue = giftLogDto.getPresentValue();
        }
    }
}
