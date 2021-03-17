package com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentCalculatedDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.EquipmentOptionDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyInventoryInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyInventoryInfoDto.EquipmentItem;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyInventoryInfoDto.InventoryItem;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.AnalysisTalentOption;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@AllArgsConstructor
public class MyInventoryInfoService {
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findUserInventory(Long userId, Map<String, Object> map) {
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        User user = userRepository.findById(userId)
                .orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<GiftBoxItemInfoTable> giftBoxItemInfoTableList = gameDataTableService.GiftBoxItemInfoTableList();
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        MyInventoryInfoDto myInventoryInfoDto = new MyInventoryInfoDto();
        List<InventoryItem> inventoryItemList = settingInventoryItem(userId, belongingInventoryList, spendableItemInfoTableList,
                equipmentMaterialInfoTableList, giftBoxItemInfoTableList, belongingCharacterPieceTableList);
        List<EquipmentItem> equipmentItemList = settingEquipmentItem(userId, heroEquipmentInventoryList, heroEquipmentsTableList,
                passiveItemTableList, equipmentOptionsInfoTableList);

        myInventoryInfoDto.setMyInventoryInfo(user.getDiamond(), user.getGold(), user.getLinkforcePoint(), user.getArenaCoin(), user.getFreeFieldDungeonTicket(),
                user.getLowDragonScale(), user.getMiddleDragonScale(), user.getHighDragonScale(), inventoryItemList, equipmentItemList);
        map.put("Inventory", myInventoryInfoDto);
        return map;
    }

    public Map<String, Object> findEquipment(Long inventoryId, Map<String, Object> map) {
        HeroEquipmentInventory heroEquipmentInventory = heroEquipmentInventoryRepository.findById(inventoryId)
                .orElse(null);
        if(heroEquipmentInventory == null) {
            errorLoggingService.SetErrorLog(inventoryId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find HeroEquipmentInventory.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find HeroEquipmentInventory.", ResponseErrorCode.NOT_FIND_DATA);
        }
        HeroEquipmentsTable heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList().stream().filter(i -> i.getId()== heroEquipmentInventory.getItem_Id())
                .findAny()
                .orElse(null);
        if(heroEquipmentsTableList == null) {
            errorLoggingService.SetErrorLog(heroEquipmentInventory.getUseridUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: not find HeroEquipmentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find HeroEquipmentsTable.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        EquipmentItem equipmentItem = new EquipmentItem();
        equipmentItem.SetEquipmentItem(heroEquipmentsTableList.getName(), heroEquipmentsTableList.getKind(), heroEquipmentsTableList.getGrade(),
                heroEquipmentInventory.getItemClass(), heroEquipmentInventory.getLevel(), heroEquipmentInventory.getExp(),
                heroEquipmentsTableList.getId(), heroEquipmentsTableList.getSetInfo(), heroEquipmentInventory.getId(),
                heroEquipmentInventory.getCreateddate(),
                getEquipmentOptions(heroEquipmentInventory.getUseridUser(), equipmentOptionsInfoTableList, gameDataTableService.HeroEquipmentsTableList(), heroEquipmentInventory,true),
                getEquipmentOptions(heroEquipmentInventory.getUseridUser(), equipmentOptionsInfoTableList, gameDataTableService.HeroEquipmentsTableList(), heroEquipmentInventory,false));
        map.put("EquipmentItem", equipmentItem);
        return map;
    }

    List<InventoryItem> settingInventoryItem(Long userId,
                                             List<BelongingInventory> belongingInventoryList,
                                             List<SpendableItemInfoTable> spendableItemInfoTableList,
                                             List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList,
                                             List<GiftBoxItemInfoTable> giftBoxItemInfoTableList,
                                             List<BelongingCharacterPieceTable> belongingCharacterPieceTableList){
        List<InventoryItem> inventoryItemList = new ArrayList<>();
        for(BelongingInventory belongingInventory:belongingInventoryList){
            InventoryItem inventoryItem = new InventoryItem();
            switch (belongingInventory.getItemType().getId().toString()){
                case "1":
                    inventoryItem.SetInventoryItem(getEquipmentMaterialInfo(userId, belongingInventory.getItemId(), equipmentMaterialInfoTableList).getName(),belongingInventory.getCount(),
                            belongingInventory.getItemId(),belongingInventory.getItemType().getId(), belongingInventory.getModifieddate());
                    break;
                case "2":
                    inventoryItem.SetInventoryItem(getGiftBoxItemInfo(userId, belongingInventory.getItemId(), giftBoxItemInfoTableList).getName(),belongingInventory.getCount(),
                            belongingInventory.getItemId(),belongingInventory.getItemType().getId(), belongingInventory.getModifieddate());
                    break;
                case "3":
                    inventoryItem.SetInventoryItem(getSpendableItemInfo(userId, belongingInventory.getItemId(), spendableItemInfoTableList).getName(),belongingInventory.getCount(),
                            belongingInventory.getItemId(),belongingInventory.getItemType().getId(), belongingInventory.getModifieddate());
                    break;
                case "4":
                    inventoryItem.SetInventoryItem(getBelongingCharacterPieceInfo(userId, belongingInventory.getItemId(), belongingCharacterPieceTableList).getName(),belongingInventory.getCount(),
                            belongingInventory.getItemId(),belongingInventory.getItemType().getId(), belongingInventory.getModifieddate());
                    break;
            }
            inventoryItemList.add(inventoryItem);
        }
        return inventoryItemList;
    }

    SpendableItemInfoTable getSpendableItemInfo(Long userId, int itemId, List<SpendableItemInfoTable> spendableItemInfoTableList){
        SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream().filter(i -> i.getId() == itemId)
                .findAny()
                .orElse(null);
        if(spendableItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: SpendableItemInfoTable not find ItemId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find ItemId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return spendableItemInfoTable;
    }

    EquipmentMaterialInfoTable getEquipmentMaterialInfo(Long userId, int itemId, List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList){
        EquipmentMaterialInfoTable equipmentMaterialInfoTable = equipmentMaterialInfoTableList.stream().filter(i -> i.getId() == itemId)
                .findAny()
                .orElse(null);
        if(equipmentMaterialInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: EquipmentMaterialInfoTable not find ItemId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find ItemId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return equipmentMaterialInfoTable;
    }

    GiftBoxItemInfoTable getGiftBoxItemInfo(Long userId, int itemId, List<GiftBoxItemInfoTable> giftBoxItemInfoTableList){
        GiftBoxItemInfoTable giftBoxItemInfoTable = giftBoxItemInfoTableList.stream().filter(i -> i.getId() == itemId)
                .findAny()
                .orElse(null);
        if(giftBoxItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: GiftBoxItemTable not find ItemId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: GiftBoxItemTable not find ItemId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return giftBoxItemInfoTable;
    }

    BelongingCharacterPieceTable getBelongingCharacterPieceInfo(Long userId, int itemId, List<BelongingCharacterPieceTable> belongingCharacterPieceTableList) {
        BelongingCharacterPieceTable belongingCharacterPieceTable = belongingCharacterPieceTableList.stream().filter(i -> i.getId() == itemId)
                .findAny()
                .orElse(null);
        if(belongingCharacterPieceTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: BelongingCharacterPieceTable not find ItemId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: BelongingCharacterPieceTable not find ItemId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return belongingCharacterPieceTable;
    }

    List<EquipmentOptionDto> getEquipmentOptions(Long userId,
                                                 List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList,
                                                 List<HeroEquipmentsTable> heroEquipmentsTableList,
                                                 HeroEquipmentInventory heroEquipmentInventory, boolean isDefaultOptions) {
        List<EquipmentOptionDto> equipmentOptionDtoList = new ArrayList<>();
        HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                .filter(i -> i.getId()==heroEquipmentInventory.getItem_Id())
                .findAny()
                .orElse(null);
        if(heroEquipmentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: HeroEquipmentsTable not find Equipment.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable not find Equipment.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String[] optionIdList = heroEquipmentInventory.getOptionIds().split(",");
        String[] optionValueList = heroEquipmentInventory.getOptionValues().split(",");
        EquipmentCalculatedDto.EquipmentInfo equipmentInfo = new EquipmentCalculatedDto.EquipmentInfo();
        equipmentInfo.setEquipmentInfo(heroEquipmentInventory, heroEquipmentsTable);
        if(isDefaultOptions){
            EquipmentOptionDto equipmentOptionDto1 = new EquipmentOptionDto();
            equipmentOptionDto1.setOption(AnalysisTalentOption.GetMainHeroEquipmentDefaultAbilityKind(equipmentInfo).toString(), equipmentInfo.decideDefaultAbilityValue, false);
            equipmentOptionDtoList.add(equipmentOptionDto1);
            EquipmentOptionDto equipmentOptionDto2 = new EquipmentOptionDto();
            equipmentOptionDto2.setOption(AnalysisTalentOption.GetMainHeroEquipmentSecondAbilityKind(equipmentInfo).toString(), equipmentInfo.decideSecondAbilityValue, false);
            equipmentOptionDtoList.add(equipmentOptionDto2);
        }else {
            for (int i = 0; i < optionIdList.length; i++) {
                if (!optionIdList[i].equals("")) {
                    int index = i;
                     EquipmentOptionsInfoTable equipmentOptionsInfoTable = equipmentOptionsInfoTableList.stream().filter(j -> j.getID() == Integer.parseInt(optionIdList[index]))
                            .findAny()
                            .orElse(null);
                    if(equipmentOptionsInfoTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: EquipmentOptionsInfoTable not find Id.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: EquipmentOptionsInfoTable not find Id.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    String optionName = equipmentOptionsInfoTable.getOption();
                    EquipmentOptionDto equipmentOptionDto = new EquipmentOptionDto();
                    equipmentOptionDto.setOption(optionName, Float.parseFloat(optionValueList[index]), true);
                    equipmentOptionDtoList.add(equipmentOptionDto);
                }
            }
        }
        return equipmentOptionDtoList;
    }

    List<EquipmentItem> settingEquipmentItem(Long userId,
                                             List<HeroEquipmentInventory> heroEquipmentInventoryList,
                                             List<HeroEquipmentsTable> heroEquipmentsTableList,
                                             List<PassiveItemTable> passiveItemTableList,
                                             List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList){

        List<EquipmentItem> equipmentItemList = new ArrayList<>();
        for(HeroEquipmentInventory heroEquipmentInventory:heroEquipmentInventoryList){
            EquipmentItem equipmentItem = new EquipmentItem();
            if(heroEquipmentInventory.getItem_Id() >= 10000){//인장
                equipmentItem.SetEquipmentItem(getPassiveItemInfo(userId, heroEquipmentInventory.getItem_Id(), passiveItemTableList).getName(),
                        "",
                        "",
                        heroEquipmentInventory.getItemClass(),
                        heroEquipmentInventory.getLevel(),
                        heroEquipmentInventory.getExp(),
                        heroEquipmentInventory.getItem_Id(),
                        0,
                        heroEquipmentInventory.getId(),
                        heroEquipmentInventory.getCreateddate(),
                        null, null);
            }
            else{
                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream().filter(i -> i.getId()==heroEquipmentInventory.getItem_Id())
                        .findAny()
                        .orElse(null);
                if(heroEquipmentsTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: HeroEquipmentsTable not find Id.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: HeroEquipmentsTable not find Id.", ResponseErrorCode.NOT_FIND_DATA);
                }
                equipmentItem.SetEquipmentItem(heroEquipmentsTable.getName(),
                        heroEquipmentsTable.getKind(),
                        heroEquipmentsTable.getGrade(),
                        heroEquipmentInventory.getItemClass(),
                        heroEquipmentInventory.getLevel(),
                        heroEquipmentInventory.getExp(),
                        heroEquipmentInventory.getItem_Id(),
                        heroEquipmentsTable.getSetInfo(),
                        heroEquipmentInventory.getId(),
                        heroEquipmentInventory.getCreateddate(),
                        getEquipmentOptions(userId, equipmentOptionsInfoTableList, heroEquipmentsTableList, heroEquipmentInventory, true),
                        getEquipmentOptions(userId, equipmentOptionsInfoTableList, heroEquipmentsTableList, heroEquipmentInventory, false));
            }

            equipmentItemList.add(equipmentItem);
        }
        return equipmentItemList;
    }

    PassiveItemTable getPassiveItemInfo(Long userId, int itemId, List<PassiveItemTable> passiveItemTableList){

        PassiveItemTable passiveItemTable = passiveItemTableList.stream().filter(i -> i.getId() == itemId)
                .findFirst()
                .orElse(null);
        if(passiveItemTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: PassiveItemTable not find ItemId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PassiveItemTable not find ItemId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return passiveItemTable;
    }
}
