package com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.HeroEquipmentInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.HeroEquipmentInfoDto.ItemInfo;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.HeroEquipmentInfoDto.Deck;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.PassiveItemTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentInfoService {
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findbyuserid(Long userId, Map<String, Object> map) {
        HeroEquipmentInfoDto heroEquipmentInfoDto = new HeroEquipmentInfoDto();
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null); // Get User Deck
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        Deck deck1 = new Deck();
        deck1.DeckSetting(settingItemInfo(myEquipmentDeck.getFirstDeckWeaponInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getFirstDeckArmorInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getFirstDeckHelmetInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getFirstDeckAccessoryInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingPassiveInfo(myEquipmentDeck.getFirstDeckPassiveInventoryId1(), heroEquipmentInventoryList, passiveItemTableList),
                settingPassiveInfo(myEquipmentDeck.getFirstDeckPassiveInventoryId2(), heroEquipmentInventoryList, passiveItemTableList));
        Deck deck2 = new Deck();
        deck2.DeckSetting(settingItemInfo(myEquipmentDeck.getSecondDeckWeaponInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getSecondDeckArmorInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getSecondDeckHelmetInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getSecondDeckAccessoryInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingPassiveInfo(myEquipmentDeck.getSecondDeckPassiveInventoryId1(), heroEquipmentInventoryList, passiveItemTableList),
                settingPassiveInfo(myEquipmentDeck.getSecondDeckPassiveInventoryId2(), heroEquipmentInventoryList, passiveItemTableList));
        Deck deck3 = new Deck();
        deck3.DeckSetting(settingItemInfo(myEquipmentDeck.getThirdDeckWeaponInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getThirdDeckArmorInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getThirdDeckHelmetInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingItemInfo(myEquipmentDeck.getThirdDeckAccessoryInventoryId(), heroEquipmentInventoryList, heroEquipmentsTableList),
                settingPassiveInfo(myEquipmentDeck.getThirdDeckPassiveInventoryId1(), heroEquipmentInventoryList, passiveItemTableList),
                settingPassiveInfo(myEquipmentDeck.getThirdDeckPassiveInventoryId2(), heroEquipmentInventoryList, passiveItemTableList));

        heroEquipmentInfoDto.setFirstDeck(deck1);
        heroEquipmentInfoDto.setSecondDeck(deck2);
        heroEquipmentInfoDto.setThirdDeck(deck3);
        map.put("UserId", userId);
        map.put("Deck", heroEquipmentInfoDto);
        return map;
    }

    ItemInfo settingItemInfo(Long itemId, List<HeroEquipmentInventory> heroEquipmentInventories, List<HeroEquipmentsTable> heroEquipmentsTableList){
        HeroEquipmentInventory inventoryInfo =  getInventoryId(itemId, heroEquipmentInventories);
        HeroEquipmentsTable iteminfo = getItemInfo(inventoryInfo.getItem_Id(), heroEquipmentsTableList);
        ItemInfo itemInfo = new HeroEquipmentInfoDto.ItemInfo();
        itemInfo.SetItemInfo(itemId,iteminfo.getName(), iteminfo.getKind(), inventoryInfo.getItemClass(), iteminfo.getGrade(), iteminfo.getSetInfo(), inventoryInfo.getDecideDefaultAbilityValue(),
                inventoryInfo.getDecideSecondAbilityValue(), inventoryInfo.getLevel(), inventoryInfo.getOptionIds(), inventoryInfo.getOptionValues());
        return itemInfo;
    }

    ItemInfo settingPassiveInfo(Long itemId, List<HeroEquipmentInventory> heroEquipmentInventories, List<PassiveItemTable> passiveItemTableList){
        HeroEquipmentInventory inventoryInfo = getInventoryId(itemId, heroEquipmentInventories);
        PassiveItemTable passiveItemTable = getPassiveItemTable(inventoryInfo.getItem_Id(), passiveItemTableList);
        ItemInfo itemInfo = new HeroEquipmentInfoDto.ItemInfo();
        itemInfo.SetItemInfo(itemId,passiveItemTable.getName(), passiveItemTable.getKIND(), inventoryInfo.getItemClass(), "", 0,
                0L,0L,0,"","" );
        return itemInfo;
    }

    HeroEquipmentInventory getInventoryId(Long deckInventoryId, List<HeroEquipmentInventory> heroEquipmentInventories){
        return heroEquipmentInventories.stream()
                .filter(inventory -> inventory.getId().equals(deckInventoryId))
                .findAny()
                .orElseGet(() -> setDefaultInventory());

    }

    HeroEquipmentsTable getItemInfo(int itemId, List<HeroEquipmentsTable> heroEquipmentsTableList){
        return heroEquipmentsTableList.stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseGet(() -> setDefaultEquipment());
    }

    PassiveItemTable getPassiveItemTable(int itemId, List<PassiveItemTable> passiveItemTableList){
        return passiveItemTableList.stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseGet(() -> setDefaultPassive());
    }

    PassiveItemTable setDefaultPassive() {
        PassiveItemTable passiveItemTable = new PassiveItemTable();
        passiveItemTable.setName("없음");
        passiveItemTable.setKIND("없음");
        return passiveItemTable;
    }

    HeroEquipmentsTable setDefaultEquipment() {
        HeroEquipmentsTable heroEquipmentsTable = new HeroEquipmentsTable();
        heroEquipmentsTable.setName("없음");
        heroEquipmentsTable.setKind("");
        heroEquipmentsTable.setGrade("");
        return heroEquipmentsTable;
    }

    HeroEquipmentInventory setDefaultInventory() {
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(0L);
        dto.setItem_Id(0);
        dto.setItemClassValue(0);
        dto.setDecideDefaultAbilityValue(0);
        dto.setDecideSecondAbilityValue(0);
        dto.setLevel(1);
        dto.setMaxLevel(1);
        dto.setExp(0);
        dto.setNextExp(0);
        dto.setItemClass("");

        HeroEquipmentInventory heroEquipmentInventory = dto.ToEntity();
        return heroEquipmentInventory;
    }
}
