package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentDivideService {
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;

    private void getEnchantCount(Long userId, int itemId, int gettingEnchantStoneCount, ItemType strengthenStoneItemType,
                                 int stackLimit, List<BelongingInventory> belongingInventoryList,
                                 List<BelongingInventoryDto> addBelongingInventoryInfoList,
                                 HeroEquipmentInventory heroEquipment,
                                 String equipmentName) {
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        BelongingInventory myAncientEnchantStone = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == itemId)
                .findAny()
                .orElse(null);

        if(myAncientEnchantStone == null) {
            belongingInventoryLogDto.setPreviousValue(0);
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(itemId);
            belongingInventoryDto.setCount(gettingEnchantStoneCount);
            belongingInventoryDto.setItemType(strengthenStoneItemType);
            myAncientEnchantStone = belongingInventoryDto.ToEntity();
            myAncientEnchantStone = belongingInventoryRepository.save(myAncientEnchantStone);
            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+equipmentName+" ["+heroEquipment.getId()+"]", myAncientEnchantStone.getId(), myAncientEnchantStone.getItemId(), myAncientEnchantStone.getItemType(), gettingEnchantStoneCount, myAncientEnchantStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
            belongingInventoryList.add(myAncientEnchantStone);
        }
        else {
            belongingInventoryLogDto.setPreviousValue(myAncientEnchantStone.getCount());
            myAncientEnchantStone.AddItem(gettingEnchantStoneCount, stackLimit);
            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+equipmentName+" ["+heroEquipment.getId()+"]", myAncientEnchantStone.getId(), myAncientEnchantStone.getItemId(), myAncientEnchantStone.getItemType(), gettingEnchantStoneCount, myAncientEnchantStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }

        BelongingInventoryDto willAddItemDto = addBelongingInventoryInfoList.stream()
                .filter( a-> a.getId().equals(itemId))
                .findAny()
                .orElse(null);
        if(willAddItemDto != null) {
            willAddItemDto.AddCount(gettingEnchantStoneCount);
        }
        else {
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setId(myAncientEnchantStone.getId());
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(itemId);
            belongingInventoryDto.setCount(gettingEnchantStoneCount);
            belongingInventoryDto.setItemType(strengthenStoneItemType);
            addBelongingInventoryInfoList.add(belongingInventoryDto);
        }
    }

    public Map<String, Object> RequestEquipmentDivide(Long userId, List<ItemRequestDto> divideItemList, Map<String, Object> map) {

        int totalEarnedGold = 0;
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<HeroEquipmentInventory> userHeroEquipmentInventory = heroEquipmentInventoryRepository.findByUseridUser(userId);
        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        /*패스 업적 : 체크 준비*/
        MyEternalPassMissionsData myEternalPassMissionsData = myEternalPassMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myEternalPassMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEternalPassMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyEternalPassMissionData = myEternalPassMissionsData.getJson_saveDataValue();
        EventMissionDataDto myEternalPassMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyEternalPassMissionData, EventMissionDataDto.class);
        boolean changedEternalPassMissionsData = false;


        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        List<PassiveItemTable> passiveItemTableList = gameDataTableService.PassiveItemTableList();
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();

        SpendableItemInfoTable ancientEnchantStone = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("enchant_006"))
                .findAny()
                .orElse(null);

        SpendableItemInfoTable heroEnchantStone = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("enchant_003"))
                .findAny()
                .orElse(null);

        SpendableItemInfoTable normalEnchantStone = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("enchant_001"))
                .findAny()
                .orElse(null);

        SpendableItemInfoTable linkweapon_bronzeKey = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("linkweapon_bronzeKey"))
                .findAny()
                .orElse(null);

        SpendableItemInfoTable linkweapon_silverKey = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("linkweapon_silverKey"))
                .findAny()
                .orElse(null);

        SpendableItemInfoTable linkweapon_goldKey = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals("linkweapon_goldKey"))
                .findAny()
                .orElse(null);

        //클라이언트로 줄 추가된 재료들에 대한 리스트
        List<BelongingInventoryDto> addBelongingInventoryInfoList = new ArrayList<>();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();

        ItemType spendAbleItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                .findAny()
                .orElse(null);

        List<HeroEquipmentInventory> divideUserItemFromDBList = new ArrayList<>();
        for(ItemRequestDto divideItem : divideItemList) {
            String gradeString = "";
            String name = "";
            HeroEquipmentInventory heroEquipment = userHeroEquipmentInventory.stream()
                    .filter(a -> a.getId().equals(divideItem.getId()))
                    .findAny()
                    .orElse(null);
            if(heroEquipment == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            if(myEquipmentDeck.IsIncludeDeckItem(heroEquipment.getId()))/*덱에 장착되어 있는 경우 분해 불가능*/ {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: material is can't included deck.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: material is can't included deck.", ResponseErrorCode.CANT_USE_MATERIAL);
            }
            if(heroEquipment.getItem_Id()>=10000) {
                PassiveItemTable passiveItemTable = passiveItemTableList.stream()
                        .filter(a -> a.getId() == heroEquipment.getItem_Id())
                        .findAny()
                        .orElse(null);
                if(passiveItemTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: PassiveItemTable Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: PassiveItemTable Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                name = passiveItemTable.getName();
                gradeString = "Ancient";
            }
            else {
                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                        .filter(a -> a.getId() == heroEquipment.getItem_Id())
                        .findAny()
                        .orElse(null);
                if (heroEquipmentsTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: HeroEquipment Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: HeroEquipment Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                name = heroEquipmentsTable.getName();
                gradeString = heroEquipmentsTable.getGrade();
            }

            int totalExp = heroEquipment.getExp();
            //획득 강화석 갯수 계산
            List<Integer> gettingStrengthenStoneList = GetStrengthenStoneFromDivide(totalExp, ancientEnchantStone, heroEnchantStone, normalEnchantStone);
            int ancientEnchantStoneCount = gettingStrengthenStoneList.get(0);
            int heroEnchantStoneCount = gettingStrengthenStoneList.get(1);
            int normalEnchantStoneCount = gettingStrengthenStoneList.get(2);

            if(ancientEnchantStoneCount > 0) {
                getEnchantCount(userId, ancientEnchantStone.getId(), ancientEnchantStoneCount, spendAbleItemType, ancientEnchantStone.getStackLimit(), belongingInventoryList, addBelongingInventoryInfoList, heroEquipment, name);
            }
            if(heroEnchantStoneCount > 0){
                getEnchantCount(userId, heroEnchantStone.getId(), heroEnchantStoneCount, spendAbleItemType, heroEnchantStone.getStackLimit(), belongingInventoryList, addBelongingInventoryInfoList, heroEquipment, name);
            }
            if(normalEnchantStoneCount > 0){
                getEnchantCount(userId, normalEnchantStone.getId(), normalEnchantStoneCount, spendAbleItemType, normalEnchantStone.getStackLimit(), belongingInventoryList, addBelongingInventoryInfoList, heroEquipment, name);
            }

            //획득 골드 계산
            int gradeValue = EquipmentCalculate.GradeValue(gradeString);
            int willGettingGold = HeroEquipmentStrengthenService.CalculateCost(totalExp, gradeValue);
            switch (gradeString) {
                case "Normal":
                    willGettingGold += 500;
                    break;
                case "Rare":
                    willGettingGold += 1000;
                    break;
                case "Hero":
                    willGettingGold += 3000;
                    break;
                case "Legend":
                    willGettingGold += 7000;
                    {
                        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                        BelongingInventory myLinkweapon_bronzeKey = belongingInventoryList.stream()
                                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweapon_bronzeKey.getId())
                                .findAny()
                                .orElse(null);

                        if (myLinkweapon_bronzeKey == null) {
                            belongingInventoryLogDto.setPreviousValue(0);
                            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                            belongingInventoryDto.setUseridUser(userId);
                            belongingInventoryDto.setItemId(linkweapon_bronzeKey.getId());
                            belongingInventoryDto.setCount(1);
                            belongingInventoryDto.setItemType(spendAbleItemType);
                            myLinkweapon_bronzeKey = belongingInventoryDto.ToEntity();
                            myLinkweapon_bronzeKey = belongingInventoryRepository.save(myLinkweapon_bronzeKey);
                            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_bronzeKey.getId(), myLinkweapon_bronzeKey.getItemId(), myLinkweapon_bronzeKey.getItemType(), 1, myLinkweapon_bronzeKey.getCount());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                            loggingService.setLogging(userId, 3, log);
                            belongingInventoryList.add(myLinkweapon_bronzeKey);
                        } else {
                            belongingInventoryLogDto.setPreviousValue(myLinkweapon_bronzeKey.getCount());
                            myLinkweapon_bronzeKey.AddItem(1, linkweapon_bronzeKey.getStackLimit());
                            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_bronzeKey.getId(), myLinkweapon_bronzeKey.getItemId(), myLinkweapon_bronzeKey.getItemType(), 1, myLinkweapon_bronzeKey.getCount());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                            loggingService.setLogging(userId, 3, log);
                        }

                        BelongingInventoryDto willAddItemDto = addBelongingInventoryInfoList.stream()
                                .filter(a -> a.getId().equals(linkweapon_bronzeKey.getId()))
                                .findAny()
                                .orElse(null);
                        if (willAddItemDto != null) {
                            willAddItemDto.AddCount(1);
                        } else {
                            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                            belongingInventoryDto.setId(myLinkweapon_bronzeKey.getId());
                            belongingInventoryDto.setUseridUser(userId);
                            belongingInventoryDto.setItemId(linkweapon_bronzeKey.getId());
                            belongingInventoryDto.setCount(1);
                            belongingInventoryDto.setItemType(spendAbleItemType);
                            addBelongingInventoryInfoList.add(belongingInventoryDto);
                        }
                    }
                    break;
                case "Divine":
                    willGettingGold += 30000;
                    {
                        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                        BelongingInventory myLinkweapon_silverKey = belongingInventoryList.stream()
                                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweapon_silverKey.getId())
                                .findAny()
                                .orElse(null);

                        if (myLinkweapon_silverKey == null) {
                            belongingInventoryLogDto.setPreviousValue(0);
                            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                            belongingInventoryDto.setUseridUser(userId);
                            belongingInventoryDto.setItemId(linkweapon_silverKey.getId());
                            belongingInventoryDto.setCount(1);
                            belongingInventoryDto.setItemType(spendAbleItemType);
                            myLinkweapon_silverKey = belongingInventoryDto.ToEntity();
                            myLinkweapon_silverKey = belongingInventoryRepository.save(myLinkweapon_silverKey);
                            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_silverKey.getId(), myLinkweapon_silverKey.getItemId(), myLinkweapon_silverKey.getItemType(), 1, myLinkweapon_silverKey.getCount());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                            loggingService.setLogging(userId, 3, log);
                            belongingInventoryList.add(myLinkweapon_silverKey);
                        } else {
                            belongingInventoryLogDto.setPreviousValue(myLinkweapon_silverKey.getCount());
                            myLinkweapon_silverKey.AddItem(1, linkweapon_silverKey.getStackLimit());
                            belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_silverKey.getId(), myLinkweapon_silverKey.getItemId(), myLinkweapon_silverKey.getItemType(), 1, myLinkweapon_silverKey.getCount());
                            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                            loggingService.setLogging(userId, 3, log);
                        }

                        BelongingInventoryDto willAddItemDto = addBelongingInventoryInfoList.stream()
                                .filter(a -> a.getId().equals(linkweapon_silverKey.getId()))
                                .findAny()
                                .orElse(null);
                        if (willAddItemDto != null) {
                            willAddItemDto.AddCount(1);
                        } else {
                            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                            belongingInventoryDto.setId(myLinkweapon_silverKey.getId());
                            belongingInventoryDto.setUseridUser(userId);
                            belongingInventoryDto.setItemId(linkweapon_silverKey.getId());
                            belongingInventoryDto.setCount(1);
                            belongingInventoryDto.setItemType(spendAbleItemType);
                            addBelongingInventoryInfoList.add(belongingInventoryDto);
                        }
                    }
                    break;
                case "Ancient":
                    willGettingGold += 100000;
                {
                    BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                    BelongingInventory myLinkweapon_goldKey = belongingInventoryList.stream()
                            .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == linkweapon_goldKey.getId())
                            .findAny()
                            .orElse(null);

                    if (myLinkweapon_goldKey == null) {
                        belongingInventoryLogDto.setPreviousValue(0);
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(linkweapon_goldKey.getId());
                        belongingInventoryDto.setCount(1);
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        myLinkweapon_goldKey = belongingInventoryDto.ToEntity();
                        myLinkweapon_goldKey = belongingInventoryRepository.save(myLinkweapon_goldKey);
                        belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_goldKey.getId(), myLinkweapon_goldKey.getItemId(), myLinkweapon_goldKey.getItemType(), 1, myLinkweapon_goldKey.getCount());
                        String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                        loggingService.setLogging(userId, 3, log);
                        belongingInventoryList.add(myLinkweapon_goldKey);
                    } else {
                        belongingInventoryLogDto.setPreviousValue(myLinkweapon_goldKey.getCount());
                        myLinkweapon_goldKey.AddItem(1, linkweapon_silverKey.getStackLimit());
                        belongingInventoryLogDto.setBelongingInventoryLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", myLinkweapon_goldKey.getId(), myLinkweapon_goldKey.getItemId(), myLinkweapon_goldKey.getItemType(), 1, myLinkweapon_goldKey.getCount());
                        String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                        loggingService.setLogging(userId, 3, log);
                    }

                    BelongingInventoryDto willAddItemDto = addBelongingInventoryInfoList.stream()
                            .filter(a -> a.getId().equals(linkweapon_goldKey.getId()))
                            .findAny()
                            .orElse(null);
                    if (willAddItemDto != null) {
                        willAddItemDto.AddCount(1);
                    } else {
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setId(myLinkweapon_goldKey.getId());
                        belongingInventoryDto.setUseridUser(userId);
                        belongingInventoryDto.setItemId(linkweapon_goldKey.getId());
                        belongingInventoryDto.setCount(1);
                        belongingInventoryDto.setItemType(spendAbleItemType);
                        addBelongingInventoryInfoList.add(belongingInventoryDto);
                    }
                }
                    break;
            }
            totalEarnedGold += willGettingGold;
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getGold();
            user.AddGold(willGettingGold);
            /* 패스 업적 : 골드획득*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", willGettingGold, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            int diviceCount = divideItemList.size();
            /* 패스 업적 : 장비 분해 갯수 체크*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.DIVIDE_EQUIPMENT.name(),"empty", diviceCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            
            currencyLogDto.setCurrencyLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]","골드", previousValue, willGettingGold, user.getGold());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            divideUserItemFromDBList.add(heroEquipment);
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
            heroEquipmentInventoryDto.InitFromDbData(heroEquipment);
            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
            equipmentLogDto.setEquipmentLogDto("장비분해 - "+name+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "제거", heroEquipmentInventoryDto);
            String quipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
            loggingService.setLogging(userId, 2, quipmentLog);

        }
        heroEquipmentInventoryRepository.deleteAll(divideUserItemFromDBList);
        map.put("totalEarnedGold", totalEarnedGold);

        map.put("addBelongingInventoryInfoList", addBelongingInventoryInfoList);

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }

        return map;
    }

    List<Integer> GetStrengthenStoneFromDivide(int totalExp,  SpendableItemInfoTable ancientEnchantStone, SpendableItemInfoTable heroEnchantStone, SpendableItemInfoTable normalEnchantStone) {

        List<Integer> gettingEnchantStoneList = new ArrayList<>();
        double remainCalculateExp = totalExp * 0.8;
        //고대 강화석
        int baseGettingExp = EquipmentCalculate.GetBaseExpFromMaterialGrade(ancientEnchantStone.getCode(), ancientEnchantStone.getGrade());
        int ancientEnchantStoneCount = (int)Math.floor(remainCalculateExp / baseGettingExp);
        remainCalculateExp = remainCalculateExp - (ancientEnchantStoneCount * baseGettingExp);
        gettingEnchantStoneList.add(ancientEnchantStoneCount);
        //영웅 강화석
        baseGettingExp = EquipmentCalculate.GetBaseExpFromMaterialGrade(heroEnchantStone.getCode(), heroEnchantStone.getGrade());
        int heroEnchantStoneCount = (int)Math.floor(remainCalculateExp / baseGettingExp);
        remainCalculateExp = remainCalculateExp - (heroEnchantStoneCount * baseGettingExp);
        gettingEnchantStoneList.add(heroEnchantStoneCount);
        //일반 강화석
        baseGettingExp = EquipmentCalculate.GetBaseExpFromMaterialGrade(normalEnchantStone.getCode(), normalEnchantStone.getGrade());
        int normalEnchantStoneCount = (int)Math.floor(remainCalculateExp / baseGettingExp);
        remainCalculateExp = remainCalculateExp - (normalEnchantStoneCount * baseGettingExp);
        gettingEnchantStoneList.add(normalEnchantStoneCount);
        return gettingEnchantStoneList;
    }
}
