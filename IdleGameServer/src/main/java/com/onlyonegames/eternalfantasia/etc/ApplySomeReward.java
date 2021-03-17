package com.onlyonegames.eternalfantasia.etc;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

public class ApplySomeReward {
    //소모품 보상 추가
    public static BelongingInventoryDto ApplySpendableItem(Long userId, String gettingItemCode, int gettingCount, BelongingInventoryRepository belongingInventoryRepository, List<BelongingInventory> belongingInventoryList, List<SpendableItemInfoTable> spendableItemInfoTableList, ItemType spendAbleItemType, String workingPosition, LoggingService loggingService, ErrorLoggingService errorLoggingService) {
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        SpendableItemInfoTable spendableItemInfo = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(spendableItemInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItemInfoTable not find.", "ApplySomeReward", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        BelongingInventory mySpendableItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == spendableItemInfo.getId())
                .findAny()
                .orElse(null);
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        if (mySpendableItem != null) {
            belongingInventoryLogDto.setPreviousValue(mySpendableItem.getCount());
            mySpendableItem.AddItem(gettingCount, spendableItemInfo.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, mySpendableItem.getId(), mySpendableItem.getItemId(), mySpendableItem.getItemType(), gettingCount, mySpendableItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            belongingInventoryDto.InitFromDbData(mySpendableItem);
            belongingInventoryDto.setCount(gettingCount);
        } else {
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(spendableItemInfo.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(spendAbleItemType);
            mySpendableItem = belongingInventoryDto.ToEntity();
            mySpendableItem = belongingInventoryRepository.save(mySpendableItem);
            belongingInventoryList.add(mySpendableItem);
            belongingInventoryDto.setId(mySpendableItem.getId());
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, mySpendableItem.getId(), mySpendableItem.getItemId(), mySpendableItem.getItemType(), gettingCount, mySpendableItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }

        return belongingInventoryDto;
    }

    //장비 제작 재료 추가
    public static BelongingInventoryDto AddEquipmentMaterialItem(String gettingItemCode, int gettingCount,
                                                                 BelongingInventoryRepository belongingInventoryRepository,
                                                                 List<ItemType> itemTypeList,
                                                                 List<BelongingInventory> belongingInventoryList,
                                                                 List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList,
                                                                 ItemType materialItemType,
                                                                 String workingPosition,
                                                                 Long userId, LoggingService loggingService, ErrorLoggingService errorLoggingService) {
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(equipmentMaterial == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", "ApplySomeReward", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        BelongingInventory inventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                .findAny()
                .orElse(null);
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();

        if(inventoryItem != null) {
            belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
            inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(inventoryItem.getUseridUser(), 3, log);
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryDto.setCount(gettingCount);
        }
        else {

            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(equipmentMaterial.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(materialItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(willAddBelongingInventoryItem.getUseridUser(), 3, log);
        }
        return belongingInventoryDto;
    }

    //종류별, 등급별, 품질별 장비 추가
    public static HeroEquipmentInventoryDto AddEquipmentItem(User user, String gettingItemCode,
                                                             List<HeroEquipmentInventory> heroEquipmentInventoryList,
                                                             HeroEquipmentInventoryRepository heroEquipmentInventoryRepository,
                                                             List<HeroEquipmentsTable> heroEquipmentsTableList,
                                                             HeroEquipmentClassProbabilityTable classValues,
                                                             List<EquipmentOptionsInfoTable> optionsInfoTableList,
                                                             String workingPosition, LoggingService loggingService, ErrorLoggingService errorLoggingService) {
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Full Inventory!", "ApplySomeReward", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        String decideGrade = "Normal";
        if(gettingItemCode.contains("Normal")) {
            decideGrade = "Normal";
        }
        else if(gettingItemCode.contains("Rare")) {
            decideGrade = "Rare";
        }
        else if(gettingItemCode.contains("Hero")) {
            decideGrade = "Hero";
        }
        else if(gettingItemCode.contains("Legend")||gettingItemCode.contains("legend")) {
            decideGrade = "Legend";
        }
        else if(gettingItemCode.contains("Divine")) {
            decideGrade = "Divine";
        }
        else if(gettingItemCode.contains("Ancient")) {
            decideGrade = "Ancient";
        }
        String itemClass = "D";
        int classValue = 0;
        if(gettingItemCode.contains("ClassD")){
            itemClass = "D";
        }
        else if(gettingItemCode.contains("ClassC")){
            itemClass = "C";
        }
        else if(gettingItemCode.contains("ClassB")){
            itemClass = "B";
        }
        else if(gettingItemCode.contains("ClassA")){
            itemClass = "A";
        }
        else if(gettingItemCode.contains("ClassSSS")){
            itemClass = "SSS";
        }
        else if(gettingItemCode.contains("ClassSS")){
            itemClass = "SS";
        }
        else if(gettingItemCode.contains("ClassS")){
            itemClass = "S";
        }
        EquipmentItemCategory equipmentItemCategory = EquipmentItemCategory.ALL;
        if(!gettingItemCode.contains("All_")&&gettingItemCode.contains("All")) {
            String temp = "D,C,B,A,S,SS,SSS";
            String[] classTemp = temp.split(",");
            int selected = (int) MathHelper.Range(0, classTemp.length-1);
            itemClass = classTemp[selected];
            equipmentItemCategory = EquipmentItemCategory.ALL;
        }
        else if(gettingItemCode.contains("Weapon")) {
            equipmentItemCategory = EquipmentItemCategory.WEAPON;
        }
        else if(gettingItemCode.contains("Armor")) {
            equipmentItemCategory = EquipmentItemCategory.ARMOR;
        }
        else if(gettingItemCode.contains("Helmet")) {
            equipmentItemCategory = EquipmentItemCategory.HELMET;
        }
        else if(gettingItemCode.contains("Accessory")) {
            equipmentItemCategory = EquipmentItemCategory.ACCESSORY;
        }

        classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);
        List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, equipmentItemCategory, decideGrade);
        int randValue = (int) MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
        HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, itemClass, classValue, optionsInfoTableList);
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        equipmentLogDto.setEquipmentLogDto(workingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
        String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(user.getId(), 2, log);
        return heroEquipmentInventoryDto;
    }

    //균열 보스용 종류별, 등급별, 품질별 장비 추가
    public static HeroEquipmentInventoryDto AddEquipmentItemForFieldBoss(User user, String gettingItemCode,
                                                             List<HeroEquipmentInventory> heroEquipmentInventoryList,
                                                             HeroEquipmentInventoryRepository heroEquipmentInventoryRepository,
                                                             List<HeroEquipmentsTable> probabilityList,
                                                             HeroEquipmentClassProbabilityTable classValues,
                                                             List<EquipmentOptionsInfoTable> optionsInfoTableList,
                                                             String workingPosition, LoggingService loggingService, ErrorLoggingService errorLoggingService) {
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Full Inventory!", "ApplySomeReward", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        String itemClass = "D";
        int classValue = 0;
        if(gettingItemCode.contains("ClassD")){
            itemClass = "D";
        }
        else if(gettingItemCode.contains("ClassC")){
            itemClass = "C";
        }
        else if(gettingItemCode.contains("ClassB")){
            itemClass = "B";
        }
        else if(gettingItemCode.contains("ClassA")){
            itemClass = "A";
        }
        else if(gettingItemCode.contains("ClassSSS")){
            itemClass = "SSS";
        }
        else if(gettingItemCode.contains("ClassSS")){
            itemClass = "SS";
        }
        else if(gettingItemCode.contains("ClassS")){
            itemClass = "S";
        }


        classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

        int randValue = (int) MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
        HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, itemClass, classValue, optionsInfoTableList);
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        equipmentLogDto.setEquipmentLogDto(workingPosition, generatedItem.getId(), "추가", heroEquipmentInventoryDto);
        String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(user.getId(), 2, log);
        return heroEquipmentInventoryDto;
    }

    //TODO 캐릭터 조각 추가 코드 추가 필요
    public static BelongingInventoryDto ApplyCharacterPiece(Long userId, String gettingItemCode, int gettingCount,
                                                            BelongingInventoryRepository belongingInventoryRepository,
                                                            List<BelongingInventory> belongingInventoryList,
                                                            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList,
                                                            ItemType belongingItemType, String workingPosition, LoggingService loggingService, ErrorLoggingService errorLoggingService) {
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        BelongingCharacterPieceTable spendableItemInfo = belongingCharacterPieceTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(spendableItemInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingCharacterPieceTable not find.", "ApplySomeReward", Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: BelongingCharacterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == spendableItemInfo.getId())
                .findAny()
                .orElse(null);
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        if (myCharacterPieceItem != null) {
            belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
            myCharacterPieceItem.AddItem(gettingCount, spendableItemInfo.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            belongingInventoryDto.InitFromDbData(myCharacterPieceItem);
            belongingInventoryDto.setCount(gettingCount);
        } else {
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(spendableItemInfo.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingItemType);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryList.add(myCharacterPieceItem);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }

        return belongingInventoryDto;
    }
}
