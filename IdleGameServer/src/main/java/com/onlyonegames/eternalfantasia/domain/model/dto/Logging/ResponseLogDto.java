package com.onlyonegames.eternalfantasia.domain.model.dto.Logging;

import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyInventoryInfoDto.EquipmentItem;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
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
    List<BelongingResponseDto> belongingResponseDtoList;
    List<EquipmentResponseDto> equipmentResponseDtoList;
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

    public static class BelongingResponseDto{
        public Long id;
        public LocalDateTime createdData;
        public String workingPosition;
        public Long inventoryId;
        public int itemId;
        public String itemName;
        public ItemType itemType_id;
        public int previousValue;
        public int changeNum;
        public int presentValue;

        public void setBelongingResponseDto(BelongingInventoryLog belongingInventoryLog, BelongingInventoryLogDto belongingInventoryLogDto, String itemName) {
            this.id = belongingInventoryLog.getId();
            this.createdData = belongingInventoryLog.getCreateddate();
            this.workingPosition = belongingInventoryLogDto.getWorkingPosition();
            this.itemName = itemName;
            this.inventoryId = belongingInventoryLogDto.getInventoryId();
            this.itemId = belongingInventoryLogDto.getItemId();
            this.itemType_id = belongingInventoryLogDto.getItemType_id();
            this.previousValue = belongingInventoryLogDto.getPreviousValue();
            this.changeNum = belongingInventoryLogDto.getChangeNum();
            this.presentValue = belongingInventoryLogDto.getPresentValue();
        }
    }

    public static class EquipmentResponseDto{
        public Long id;
        public LocalDateTime createdData;
        public String workingPosition;
        public Long equipmentInventoryId;
        public String changeStatus;
        public EquipmentItem equipmentItem;

        public void setEquipmentResponseDto(EquipmentLog equipmentLog, EquipmentLogDto equipmentLogDto, EquipmentItem equipmentItem) {
            this.id = equipmentLog.getId();
            this.createdData = equipmentLog.getCreateddate();
            this.workingPosition = equipmentLogDto.getWorkingPosition();
            this.equipmentInventoryId = equipmentLogDto.getEquipmentInventoryId();
            this.changeStatus = equipmentLogDto.getChangeStatus();
            this.equipmentItem = equipmentItem;
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
