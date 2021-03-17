package com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.CreateEquipmentAssignmentDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.PassiveItemTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;
import static com.onlyonegames.eternalfantasia.etc.EquipmentCalculate.CalculateEquipmentValue;

@Service
@AllArgsConstructor
public class EquipmentPaymentService {
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> createEquipmentAssignment(CreateEquipmentAssignmentDto dto, Map<String, Object> map){
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        String optionIds = new String();
        String optionValues = new String();
        int a = 0;
        for(Integer optionId:dto.getOptionIds()){
            if(a == 0)
                optionIds = optionId.toString();
            else
                optionIds = optionIds + "," + optionId.toString();
            a++;
        }
        a = 0;
        for(Double optionValue : dto.getOptionValues()){
            if(a == 0)
                optionValues = optionValue.toString();
            else
                optionValues = optionValues + "," + optionValue.toString();
            a++;
        }
        if(dto.getItem_Id()<10000){
            HeroEquipmentsTable heroEquipment = gameDataTableService.HeroEquipmentsTableList()
                    .stream()
                    .filter(i -> i.getId() == dto.getItem_Id())
                    .findAny()
                    .orElse(heroEquipment = null);
            if(heroEquipment == null){
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: HeroEquipmentItem not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: HeroEquipmentItem not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            HeroEquipmentInventory generatedItem = CreateEquipmentAssignment(dto.getUserId(), heroEquipment,dto.getLevel(),dto.getItemClass(),optionIds,optionValues, map);
            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        }
        else{
            PassiveItemTable passiveItemTable = gameDataTableService.PassiveItemTableList()
                    .stream()
                    .filter(i -> i.getId() == dto.getItem_Id())
                    .findAny()
                    .orElse(passiveItemTable = null);
            if(passiveItemTable == null){
                errorLoggingService.SetErrorLog(dto.getUserId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: PassiveItemTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: PassiveItemTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            HeroEquipmentInventory generatedItem = CreatePassiveitemAssignment(dto.getUserId(),passiveItemTable,dto.getItemClass(), map);
            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        }
        map.put("DTO",heroEquipmentInventoryDto);
        return map;
    }

    HeroEquipmentInventory CreateEquipmentAssignment(Long userId, HeroEquipmentsTable heroEquipment, int level, String itemClass, String optionIds, String optionsValue, Map<String, Object> map){
        String itemGradeString = heroEquipment.getGrade();
        int gradeValue = GradeValue(itemGradeString, userId);
        float decideDefaultValue = (float)CalculateEquipmentValue(heroEquipment.getDefaultAbilityValue(), level, gradeValue, GetClassValue(itemClass, userId), heroEquipment.getDefaultGrow(), heroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        float decideSecondValue = (float)CalculateEquipmentValue(heroEquipment.getSecondAbilityValue(), level, gradeValue, GetClassValue(itemClass, userId), heroEquipment.getSecondGrow(), heroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        int nextExp = GetNextLevelUpExp(level, gradeValue, MaxLevel(itemGradeString, userId));
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(userId);
        dto.setItem_Id(heroEquipment.getId());
        dto.setItemClassValue(GetClassValue(itemClass, userId));
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        dto.setDecideSecondAbilityValue(decideSecondValue);
        dto.setLevel(level);
        dto.setMaxLevel(MaxLevel(itemGradeString, userId));
        dto.setExp(GetNextLevelUpExp(level-1, gradeValue, MaxLevel(itemGradeString, userId)));
        dto.setNextExp(nextExp);
        dto.setItemClass(itemClass);
        dto.setOptionIds(optionIds);
        dto.setOptionValues(optionsValue);
        //장비 등급에 따라 옵션 결정
        return dto.ToEntity();
    }

    HeroEquipmentInventory CreatePassiveitemAssignment(Long userId, PassiveItemTable passiveItemTable, String itemClass, Map<String, Object> map){
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(userId);
        dto.setItem_Id(passiveItemTable.getId());
        dto.setItemClassValue(GetClassValue(itemClass, userId));
        dto.setDecideDefaultAbilityValue(0);
        dto.setDecideSecondAbilityValue(0);
        dto.setLevel(1);
        dto.setMaxLevel(1);
        dto.setExp(0);
        dto.setNextExp(0);
        dto.setItemClass(itemClass);
        dto.setOptionIds(null);
        dto.setOptionValues(null);
        //장비 등급에 따라 옵션 결정
        return dto.ToEntity();
    }

    int GetNextLevelUpExp(int nowLv, int grade, int maxLevel) {
        if (nowLv == maxLevel)
            nowLv--;
        int tempLv = nowLv;
        int resultExp = 0;
        while(tempLv >= 1) {
            resultExp += EquipmentCalculate.CalculateNeedExp(tempLv, grade);
            tempLv--;
        }
        return nowLv >= 1 ? resultExp : 0;
    }

    int GetClassValue(String itemClass, Long userId){
        int returnValue;
        switch (itemClass) {
            case "D":
                returnValue = 1;
                break;
            case "C":
                returnValue = 2;
                break;
            case "B":
                returnValue = 3;
                break;
            case "A":
                returnValue = 4;
                break;
            case "S":
                returnValue = 5;
                break;
            case "SS":
                returnValue = 6;
                break;
            case "SSS":
                returnValue = 7;
                break;
            default:
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Undefined itemGrade.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }

    int GradeValue(String itemGradeString, Long userId) {
        int returnValue;
        switch (itemGradeString) {
            case "Normal":
                returnValue = 1;
                break;
            case "Rare":
                returnValue = 2;
                break;
            case "Hero":
                returnValue = 3;
                break;
            case "Legend":
                returnValue = 4;
                break;
            case "Divine":
                returnValue = 5;
                break;
            case "Ancient":
                returnValue = 6;
                break;
            default:
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Undefined itemGrade.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }
//    double CalculateEquipmentValue(double baseValue, int level, int gradeValue, int classValue, double growValue) {
//
//        double sum = ((baseValue + (baseValue * ((classValue - 1) * 0.5))) + (baseValue * ((level - 1) * growValue))) + (baseValue * (gradeValue - 1) * 0.5);
//        double value = Math.ceil(sum);
//        return value;
//    }

    int MaxLevel(String itemGradeString, Long userId) {
        int returnValue = 0;
        switch (itemGradeString) {
            case "Normal":
                returnValue = 10;
                break;
            case "Rare":
                returnValue = 20;
                break;
            case "Hero":
                returnValue = 30;
                break;
            case "Legend":
                returnValue = 40;
                break;
            case "Divine":
                returnValue = 50;
                break;
            case "Ancient":
                returnValue = 60;
                break;
            default:
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Undefined itemGrade.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }
}
