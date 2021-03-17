package com.onlyonegames.eternalfantasia.domain.service.Mission;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyMissionsDataService {
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final UserRepository userRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    public Map<String, Object> GetMyMissionData(Long userId, Map<String, Object> map) {

        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MissionsDataDto missionsDataDto = JsonStringHerlper.ReadValueFromJson(myMissionsData.getJson_saveDataValue(), MissionsDataDto.class);

        boolean missionReseted = false;
        //미션 리셋 체크
        if(myMissionsData.IsResetDailyMissionClearTime()) {
            missionsDataDto.DailyMissionsReset();
            missionReseted = true;
        }
        if(myMissionsData.IsResetWeeklyMissionClearTime()) {
            missionsDataDto.WeeklyMissionsReset();
            if(!missionReseted)
                missionReseted = true;
        }
        if(missionReseted) {
            String jsonMissionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMissionData);
        }

        missionsDataDto.questMissionsData = missionsDataDto.ImportQuestMissionSendToClient(gameDataTableService.QuestMissionTableList());
        map.put("missionsDataDto", missionsDataDto);
        map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
        map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        return map;
    }
    private BelongingInventoryDto AddEquipmentMaterialItem(Long userId, String gettingItemCode, int gettingCount, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList, List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList, int previousCount) {
        EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(equipmentMaterial == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EquipmentMaterialInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EquipmentMaterialInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        BelongingInventory inventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                .findAny()
                .orElse(null);
        if(inventoryItem != null) {
            previousCount = inventoryItem.getCount();
            inventoryItem.AddItem(gettingCount, equipmentMaterial.getStackLimit());
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryDto.setCount(gettingCount);
            return belongingInventoryDto;
        }
        else {
            ItemType materialItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(materialItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(equipmentMaterial.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(materialItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            return belongingInventoryDto;
        }
    }
    /* 소모품의 경우에는 한번의 업적 클리어로 다양한 종류가 획득 될수 있으므로 호출하는 쪽에서 리스트를 만들고 mapping 시켜 리턴한다. */
    private Map<String, Object> receiveReward(Long userId, String gettingCode, int gettingCount, List<BelongingInventoryDto> changedBelongingInventoryList, Map<String, Object> map, String missionCode) {

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }

        switch (gettingCode) {
            //피로도 50 회복 물약.
            case "recovery_fatigability": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable potion_fatigabilityInfo = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(gettingCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory potion_fatigability = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == potion_fatigabilityInfo.getId())
                        .findAny()
                        .orElse(null);

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                if (potion_fatigability == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(potion_fatigabilityInfo.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    potion_fatigability = belongingInventoryDto.ToEntity();
                    potion_fatigability = belongingInventoryRepository.save(potion_fatigability);
                    belongingInventoryList.add(potion_fatigability);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", potion_fatigability.getId(),
                            potion_fatigability.getItemId(),potion_fatigability.getItemType(),gettingCount,potion_fatigability.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(potion_fatigability.getCount());
                    potion_fatigability.AddItem(gettingCount, potion_fatigabilityInfo.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", potion_fatigability.getId(),
                            potion_fatigability.getItemId(),potion_fatigability.getItemType(),gettingCount,potion_fatigability.getCount());
                }
                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(potion_fatigability);
                belongingInventoryDto.setCount(gettingCount);
                changedBelongingInventoryList.add(belongingInventoryDto);

                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);

            }
                break;
            //즉시 제작권.
            case "ticket_direct_production_equipment": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable ticket_direct_production_equipmentInfo = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(gettingCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory ticket_direct_production_equipment = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == ticket_direct_production_equipmentInfo.getId())
                        .findAny()
                        .orElse(null);

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                if (ticket_direct_production_equipment == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(ticket_direct_production_equipmentInfo.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    ticket_direct_production_equipment = belongingInventoryDto.ToEntity();
                    ticket_direct_production_equipment = belongingInventoryRepository.save(ticket_direct_production_equipment);
                    belongingInventoryList.add(ticket_direct_production_equipment);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", ticket_direct_production_equipment.getId(),
                            ticket_direct_production_equipment.getItemId(),ticket_direct_production_equipment.getItemType(),gettingCount,ticket_direct_production_equipment.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(ticket_direct_production_equipment.getCount());
                    ticket_direct_production_equipment.AddItem(gettingCount, ticket_direct_production_equipmentInfo.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", ticket_direct_production_equipment.getId(),
                            ticket_direct_production_equipment.getItemId(),ticket_direct_production_equipment.getItemType(),gettingCount,ticket_direct_production_equipment.getCount());
                }
                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(ticket_direct_production_equipment);
                belongingInventoryDto.setCount(gettingCount);
                changedBelongingInventoryList.add(belongingInventoryDto);

                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
                break;
            //고급 뽑기 1회 가능.
            case "dimensionStone": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable dimensionStoneInfo = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(gettingCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory dimensionStone = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == dimensionStoneInfo.getId())
                        .findAny()
                        .orElse(null);

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                if (dimensionStone == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(dimensionStoneInfo.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    dimensionStone = belongingInventoryDto.ToEntity();
                    dimensionStone = belongingInventoryRepository.save(dimensionStone);
                    belongingInventoryList.add(dimensionStone);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", dimensionStone.getId(),
                            dimensionStone.getItemId(),dimensionStone.getItemType(),gettingCount,dimensionStone.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(dimensionStone.getCount());
                    dimensionStone.AddItem(gettingCount, dimensionStoneInfo.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", dimensionStone.getId(),
                            dimensionStone.getItemId(),dimensionStone.getItemType(),gettingCount,dimensionStone.getCount());
                }
                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(dimensionStone);
                belongingInventoryDto.setCount(gettingCount);
                changedBelongingInventoryList.add(belongingInventoryDto);

                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
                break;
            case "gold": {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
                int previousValue = user.getGold();
                user.AddGold(gettingCount);
                currencyLogDto.setCurrencyLogDto(missionCode+" 완료 보상 획득", "골드", previousValue, gettingCount, user.getGold());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("user", user);
            }
            break;
            case "diamond": {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
                int previousValue = user.getDiamond();
                user.AddDiamond(gettingCount);
                currencyLogDto.setCurrencyLogDto(missionCode+" 완료 보상 획득", "다이아", previousValue, gettingCount, user.getDiamond());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("user", user);
            }
            break;
            case "linkPoint": {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
                int previousValue = user.getLinkforcePoint();
                user.AddLinkforcePoint(gettingCount);
                currencyLogDto.setCurrencyLogDto(missionCode+" 완료 보상 획득", "링크포인트", previousValue, gettingCount, user.getLinkforcePoint());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("user", user);
            }
            break;
            case "reward_material_low":
            case "reward_material_middle": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                int kindCount = 0;
                if (gettingCode.contains("low")) {
                    //3종
                    kindCount = 3;
                } else if (gettingCode.contains("middle")) {
                    //5종
                    kindCount = 5;
                }
                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();
                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
                List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
                copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
                for(int i = 0; i < kindCount; i++) {
                    int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                    EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                    int previousCount = 0;
                    BelongingInventoryDto belongingInventoryDto = AddEquipmentMaterialItem(userId, equipmentMaterialInfoTable.getCode(), gettingCount, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, previousCount);
                    changedBelongingInventoryList.add(belongingInventoryDto);
                    copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                    belongingInventoryLogDto.setPreviousValue(previousCount);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", belongingInventoryDto.getId(), belongingInventoryDto.getItemId(), belongingInventoryDto.getItemType(), gettingCount, belongingInventoryDto.getCount());
                    belongingInventoryLogDto.setItemType_id(belongingInventoryDto.getItemType());
                    String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                    loggingService.setLogging(userId,3,belongingLog);
                }
            }
                break;
            case "enchant_003":
            case "enchant_004":
            case "enchant_005":
            case "enchant_006": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable enchantStone = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(gettingCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myEnchantStone = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == enchantStone.getId())
                        .findAny()
                        .orElse(null);

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                if (myEnchantStone == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(enchantStone.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    myEnchantStone = belongingInventoryDto.ToEntity();
                    myEnchantStone = belongingInventoryRepository.save(myEnchantStone);
                    belongingInventoryList.add(myEnchantStone);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myEnchantStone.getId(),
                            myEnchantStone.getItemId(),myEnchantStone.getItemType(),gettingCount,myEnchantStone.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(myEnchantStone.getCount());
                    myEnchantStone.AddItem(gettingCount, enchantStone.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myEnchantStone.getId(),
                            myEnchantStone.getItemId(),myEnchantStone.getItemType(),gettingCount,myEnchantStone.getCount());
                }
                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();

                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(myEnchantStone);
                belongingInventoryDto.setCount(gettingCount);
                changedBelongingInventoryList.add(belongingInventoryDto);

                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
                break;
            case "giftAll": {
                GiftLogDto giftLogDto = new GiftLogDto();
                List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
                List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
                copyGiftTableList.remove(25);
                int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
                GiftTable giftTable = copyGiftTableList.get(randIndex);

                MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myGiftInventory == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                String inventoryInfosString = myGiftInventory.getInventoryInfos();
                GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);


                GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if(inventoryItemDto == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                }
                giftLogDto.setPreviousValue(inventoryItemDto.count);

                inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
                inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
                myGiftInventory.ResetInventoryInfos(inventoryInfosString);
                giftLogDto.setGiftLogDto(missionCode+" 완료 보상 획득",inventoryItemDto.code,gettingCount,inventoryItemDto.count);
                String giftLog = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                loggingService.setLogging(userId, 4, giftLog);

                GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
                responseItemDto.code = inventoryItemDto.code;
                responseItemDto.count = gettingCount;
                map.put("gift", responseItemDto);
            }
                break;
            case "characterPiece_cr_004":
            case "characterPiece_cr_007":
            case "characterPiece_cr_001":
            case "characterPiece_cr_000":
            case "characterPiece_cr_006":
            case "characterPiece_cr_003":
            case "characterPiece_cr_005":
            case "characterPiece_cr_015":
            case "characterPiece_cr_014":
            case "characterPiece_cr_013":
            case "characterPiece_cr_011":
            case "characterPiece_cr_012":
            case "characterPiece_cr_002":
            case "characterPiece_cr_019": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                String characterCode = gettingCode;/**characterCode replace edit*///.replace("characterPiece_", "");
                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().equals(characterCode))
                        .findAny()
                        .orElse(null);
                if(characterPiece == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
                }

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(characterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myCharacterPieceItem.getId(),
                            myCharacterPieceItem.getItemId(),myCharacterPieceItem.getItemType(),gettingCount,myCharacterPieceItem.getCount());
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, characterPiece.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myCharacterPieceItem.getId(),
                            myCharacterPieceItem.getItemId(),myCharacterPieceItem.getItemType(),gettingCount,myCharacterPieceItem.getCount());
                }
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
                BelongingInventoryDto myCharacterPieceItemDto = new BelongingInventoryDto();
                myCharacterPieceItemDto.InitFromDbData(myCharacterPieceItem);
                myCharacterPieceItemDto.setCount(gettingCount);
                map.put("myCharacterPieceItem", myCharacterPieceItemDto);
            }
                break;
            case "characterPieceAll": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();

               // List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
                int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
                BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);

                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                        .findAny()
                        .orElse(null);

                if(myCharacterPieceItem == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                    myCharacterPieceItem = belongingInventoryDto.ToEntity();
                    myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                    belongingInventoryList.add(myCharacterPieceItem);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myCharacterPieceItem.getId(),
                            myCharacterPieceItem.getItemId(),myCharacterPieceItem.getItemType(),gettingCount,myCharacterPieceItem.getCount());
                }
                else {
                    belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                    myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myCharacterPieceItem.getId(),
                            myCharacterPieceItem.getItemId(),myCharacterPieceItem.getItemType(),gettingCount,myCharacterPieceItem.getCount());
                }
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
                BelongingInventoryDto myCharacterPieceItemDto = new BelongingInventoryDto();
                myCharacterPieceItemDto.InitFromDbData(myCharacterPieceItem);
                myCharacterPieceItemDto.setCount(gettingCount);
                map.put("myCharacterPieceItem", myCharacterPieceItemDto);
            }
                break;
            case "resmelt_003":
            case "resmelt_004":
            case "resmelt_005":
            case "resmelt_006": {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                List<ItemType> itemTypeList = itemTypeRepository.findAll();
                ItemType spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);

                List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
                SpendableItemInfoTable resmeltStone = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(gettingCode))
                        .findAny()
                        .orElse(null);

                List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
                BelongingInventory myResmeltStone = belongingInventoryList.stream()
                        .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == resmeltStone.getId())
                        .findAny()
                        .orElse(null);

                if (myResmeltStone == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setUseridUser(userId);
                    belongingInventoryDto.setItemId(resmeltStone.getId());
                    belongingInventoryDto.setCount(gettingCount);
                    belongingInventoryDto.setItemType(spendAbleItemType);
                    myResmeltStone = belongingInventoryDto.ToEntity();
                    myResmeltStone = belongingInventoryRepository.save(myResmeltStone);
                    belongingInventoryList.add(myResmeltStone);
                    belongingInventoryLogDto.setPreviousValue(0);
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myResmeltStone.getId(),
                            myResmeltStone.getItemId(),myResmeltStone.getItemType(),gettingCount,myResmeltStone.getCount());
                } else {
                    belongingInventoryLogDto.setPreviousValue(myResmeltStone.getCount());
                    myResmeltStone.AddItem(gettingCount, resmeltStone.getStackLimit());
                    belongingInventoryLogDto.setBelongingInventoryLogDto(missionCode+" 완료 보상 획득", myResmeltStone.getId(),
                            myResmeltStone.getItemId(),myResmeltStone.getItemType(),gettingCount,myResmeltStone.getCount());
                }

                if(changedBelongingInventoryList == null)
                    changedBelongingInventoryList = new ArrayList<>();
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(myResmeltStone);
                belongingInventoryDto.setCount(gettingCount);
                changedBelongingInventoryList.add(belongingInventoryDto);

                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
            }
                break;
            case "arenaCoin": {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
                int previousValue = user.getArenaCoin();
                user.AddArenaCoin(gettingCount);
                currencyLogDto.setCurrencyLogDto(missionCode+" 완료 보상 획득", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                map.put("user", user);
            }
                break;
            case "equipmentAll": {
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
                //장비
                List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
                if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
                }
                //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
                //품질	D	C	B	A	S	SS	SSS
                //확률	15%	25%	45%	9%	5%	1%	0%
                List<Double> gradeProbabilityList = new ArrayList<>();
                gradeProbabilityList.add(70D);
                gradeProbabilityList.add(20D);
                gradeProbabilityList.add(9D);
                gradeProbabilityList.add(1D);
                String selectedGrade = "";
                int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedGrade = "Hero";
                        break;
                    case 1:
                        selectedGrade = "Legend";
                        break;
                    case 2:
                        selectedGrade = "Divine";
                        break;
                    case 3:
                        selectedGrade = "Ancient";
                        break;
                }

                List<Double> classProbabilityList = new ArrayList<>();
                classProbabilityList.add(15D);
                classProbabilityList.add(25D);
                classProbabilityList.add(45D);
                classProbabilityList.add(9D);
                classProbabilityList.add(5D);
                classProbabilityList.add(1D);
                classProbabilityList.add(0D);
                String selectedClass = "";
                selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
                switch (selectedIndex) {
                    case 0:
                        selectedClass = "D";
                        break;
                    case 1:
                        selectedClass = "C";
                        break;
                    case 2:
                        selectedClass = "B";
                        break;
                    case 3:
                        selectedClass = "A";
                        break;
                    case 4:
                        selectedClass = "S";
                        break;
                    case 5:
                        selectedClass = "SS";
                        break;
                    case 6:
                        selectedClass = "SSS";
                        break;
                }
                List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
                HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
                int classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
                List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
                int randValue = (int)MathHelper.Range(0, probabilityList.size());
                HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
                List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
                HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
                generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
                /*업적 : 체크 준비*/
                MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                        .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
                String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
                MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
                boolean changedMissionsData = false;

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                HeroEquipmentInventoryDto generatedItemDto = new HeroEquipmentInventoryDto();
                generatedItemDto.InitFromDbData(generatedItem);
                equipmentLogDto.setEquipmentLogDto(missionCode+" 완료 보상 획득",generatedItem.getId(),"추가",generatedItemDto);
                String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, log);
                map.put("generatedItem", generatedItemDto);
            }
               break;
        }
        return map;
    }

    public Map<String, Object> CompleteMission(Long userId, String missionCode, Map<String, Object> map) {

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

        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        MissionsDataDto missionsDataDto = JsonStringHerlper.ReadValueFromJson(myMissionsData.getJson_saveDataValue(), MissionsDataDto.class);

        List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();

        //미션 리셋 체크
        boolean missionReseted = false;
        //미션 리셋 체크
        if(myMissionsData.IsResetDailyMissionClearTime()) {
            missionsDataDto.DailyMissionsReset();
            missionReseted = true;
        }
        if(myMissionsData.IsResetWeeklyMissionClearTime()) {
            missionsDataDto.WeeklyMissionsReset();
            if(!missionReseted)
                missionReseted = true;
        }
        if(missionReseted) {
            map.put("resetMissions", true);

            String jsonMissionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMissionData);
            missionsDataDto.questMissionsData = missionsDataDto.ImportQuestMissionSendToClient(gameDataTableService.QuestMissionTableList());
            map.put("missionsDataDto", missionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());

            return map;
        }

        if(missionCode.contains("daily_mission")){
            String[] split = missionCode.split("_");
            MissionsDataDto.MissionData dailyMission = missionsDataDto.dailyMissionsData.stream()
                    .filter(missionData -> missionData.code.equals(missionCode))
                    .findAny()
                    .orElse(null);
            if(dailyMission == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: dailyMission not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(!dailyMission.success) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.MISSION_NOT_YET_COMPLETE.getIntegerValue(), "Fail! -> Cause: dailyMission not yet complete", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission not yet complete", ResponseErrorCode.MISSION_NOT_YET_COMPLETE);
            }
            if(dailyMission.rewardReceived) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: dailyMission already received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission already received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
            }

            DailyMissionTable missionTable = gameDataTableService.DailyMissionTableList().stream()
                   .filter(dailyMissionTable -> dailyMissionTable.getCode().equals(missionCode))
                   .findAny()
                   .orElse(null);
            if(missionTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: missionTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: missionTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            dailyMission.rewardReceived = true;
            missionsDataDto.dailyRewardPoint += missionTable.getGettingPoint();
            receiveReward(userId, missionTable.getGettingItem(), missionTable.getRewardCount(), changedBelongingInventoryList, map, "일일미션 "+split[2]);
            if(missionTable.getGettingItem().equals("gold")) {
                /* 패스 업적 : 골드획득*/
                changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", missionTable.getRewardCount(), gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            }
        }
        else if(missionCode.contains("weekly_mission")) {
            String[] split = missionCode.split("_");
            MissionsDataDto.MissionData weeklyMission = missionsDataDto.weeklyMissionsData.stream()
                    .filter(missionData -> missionData.code.equals(missionCode))
                    .findAny()
                    .orElse(null);
            if(weeklyMission == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: weeklyMission not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: weeklyMission not find", ResponseErrorCode.NOT_FIND_DATA);
            }

            if(!weeklyMission.success) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.MISSION_NOT_YET_COMPLETE.getIntegerValue(), "Fail! -> Cause: dailyMission not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission not find", ResponseErrorCode.MISSION_NOT_YET_COMPLETE);
            }
            if(weeklyMission.rewardReceived) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: dailyMission aready received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission aready received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
            }

            WeeklyMissionTable missionTable = gameDataTableService.WeeklyMissionTableList().stream()
                    .filter(weeklyMissionTable -> weeklyMissionTable.getCode().equals(missionCode))
                    .findAny()
                    .orElse(null);
            if(missionTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: missionTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: missionTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            weeklyMission.rewardReceived = true;
            missionsDataDto.weeklyRewardPoint += missionTable.getGettingPoint();
            receiveReward(userId, missionTable.getGettingItem(), missionTable.getRewardCount(), changedBelongingInventoryList, map, "주간미션 "+split[2]);
            if(missionTable.getGettingItem().equals("gold")) {
                /* 패스 업적 : 골드획득*/
                changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", missionTable.getRewardCount(), gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            }
        }
        else if(missionCode.contains("quest_mission")) {
            String[] split = missionCode.split("_");
            MissionsDataDto.MissionData questMission = missionsDataDto.questMissionsData.stream()
                    .filter(missionData -> missionData.code.equals(missionCode))
                    .findAny()
                    .orElse(null);
            if(questMission == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: questMissionTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: questMissionTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }

            if(!questMission.success) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.MISSION_NOT_YET_COMPLETE.getIntegerValue(), "Fail! -> Cause: dailyMission not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission not find", ResponseErrorCode.MISSION_NOT_YET_COMPLETE);
            }
            if(questMission.rewardReceived) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: dailyMission already received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: dailyMission already received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
            }

            QuestMissionTable missionTable = gameDataTableService.QuestMissionTableList().stream()
                    .filter(questMissionTable -> questMissionTable.getCode().equals(missionCode))
                    .findAny()
                    .orElse(null);
            if(missionTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: missionTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: missionTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            questMission.rewardReceived = true;

            String[] gettingItemsArray = missionTable.getGettingItem().split(",");
            for(String gettingItem : gettingItemsArray){
                String[] gettingItemInfoArray = gettingItem.split(":");
                String gettingItemCode = gettingItemInfoArray[0];
                int gettingCount = Integer.parseInt(gettingItemInfoArray[1]);
                receiveReward(userId, gettingItemCode, gettingCount, changedBelongingInventoryList, map, "메인미션 "+split[2]);

                if(gettingItemCode.equals("gold")) {
                    /* 패스 업적 : 골드획득*/
                    changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                }
            }
            //다음 스택 설정
//            String[] missionCodeArray = missionCode.split("-");
//            int stack = Integer.parseInt(missionCodeArray[1]);
//            int nextStack = stack + 1;
//            StringMaker.Clear();
//            StringMaker.stringBuilder.append(missionCodeArray[0]);
//            StringMaker.stringBuilder.append("-");
//            StringMaker.stringBuilder.append(nextStack);
//            String nextMissionCode = StringMaker.stringBuilder.toString();
//            QuestMissionTable nextMissionTable = gameDataTableService.QuestMissionTableList().stream()
//                    .filter(questMissionTable -> questMissionTable.getCode().equals(nextMissionCode))
//                    .findAny()
//                    .orElse(null);
//            if(nextMissionTable != null) {
//
//                questMission.code = nextMissionTable.getCode();
//                questMission.success = false;
//                questMission.actionCount = 0;
//                questMission.goalCount = nextMissionTable.getGoalCount();
//                questMission.rewardReceived = false;
//                if(questMission.alreadyOpenStepMax >= nextStack) {
//                    questMission.success = true;
//                    questMission.actionCount = questMission.goalCount;
//                }
//            }
        }
        else {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: MissionCode not Exist", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MissionCode not Exist", ResponseErrorCode.NOT_EXIST_CODE);
        }
        String json_missionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
        myMissionsData.ResetSaveDataValue(json_missionData);
        map.put("changedBelongingInventoryList", changedBelongingInventoryList);
        missionsDataDto.questMissionsData = missionsDataDto.ImportQuestMissionSendToClient(gameDataTableService.QuestMissionTableList());
        map.put("missionsDataDto", missionsDataDto);
        map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
        map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());

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

    public Map<String, Object> GetBonusReward(Long userId, String code, Map<String, Object> map) {

        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
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

        List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();

        MissionsDataDto missionsDataDto = JsonStringHerlper.ReadValueFromJson(myMissionsData.getJson_saveDataValue(), MissionsDataDto.class);

        //미션 리셋 체크
        boolean missionReseted = false;
        //미션 리셋 체크
        if(myMissionsData.IsResetDailyMissionClearTime()) {
            missionsDataDto.DailyMissionsReset();
            missionReseted = true;
        }
        if(myMissionsData.IsResetWeeklyMissionClearTime()) {
            missionsDataDto.WeeklyMissionsReset();
            if(!missionReseted)
                missionReseted = true;
        }
        if(missionReseted) {
            map.put("resetMissions", true);

            String jsonMissionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMissionData);
            missionsDataDto.questMissionsData = missionsDataDto.ImportQuestMissionSendToClient(gameDataTableService.QuestMissionTableList());
            map.put("missionsDataDto", missionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());

            return map;
        }

        if(code.contains("daily_mission_reward")) {
            String[] split = code.split("_");

            MissionsDataDto.RewardInfoData rewardInfoData = missionsDataDto.dailyRewardInfo.stream()
                    .filter(a -> a.code.equals(code))
                    .findAny()
                    .orElse(null);
            if(rewardInfoData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: DailyMissionRewardTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: DailyMissionRewardTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }

            if(rewardInfoData.rewardReceived) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: daily_mission_reward already received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: daily_mission_reward already received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
            }
            List<DailyMissionRewardTable> dailyMissionRewardTableList = gameDataTableService.DailyMissionRewardTableList();
            DailyMissionRewardTable tableInfo = dailyMissionRewardTableList.stream()
                    .filter(dailyMissionRewardTable -> dailyMissionRewardTable.getCode().equals(code))
                    .findAny()
                    .orElse(null);
            if(tableInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: DailyMissionRewardTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: DailyMissionRewardTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(tableInfo.getNeedPoint() > missionsDataDto.dailyRewardPoint) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_GETTING_BONUS_REWARD.getIntegerValue(), "Fail! -> Cause: Not yet getting bonus reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not yet getting bonus reward", ResponseErrorCode.NOT_YET_GETTING_BONUS_REWARD);
            }
            receiveReward(userId, tableInfo.getGettingItem(), tableInfo.getRewardCount(), changedBelongingInventoryList, map, "일일미션 "+split[2]);
            if(tableInfo.getGettingItem().equals("gold")) {
                /* 패스 업적 : 골드획득*/
                changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", tableInfo.getRewardCount(), gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            }
            rewardInfoData.rewardReceived = true;
        }
        else if(code.contains("weekly_mission_reward")){
            String[] split = code.split("_");

            MissionsDataDto.RewardInfoData rewardInfoData = missionsDataDto.weeklyRewardInfo.stream()
                    .filter(a -> a.code.equals(code))
                    .findAny()
                    .orElse(null);
            if(rewardInfoData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: WeeklyMissionRewardTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: WeeklyMissionRewardTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(rewardInfoData.rewardReceived) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD.getIntegerValue(), "Fail! -> Cause: weekly_mission_reward already received reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: weekly_mission_reward already received reward", ResponseErrorCode.ALREADY_RECEIVED_MISSION_REWARD);
            }
            List<WeeklyMissionRewardTable> weeklyMissionRewardTableList = gameDataTableService.WeeklyMissionRewardTableList();
            WeeklyMissionRewardTable tableInfo = weeklyMissionRewardTableList.stream()
                    .filter(weeklyMissionRewardTable -> weeklyMissionRewardTable.getCode().equals(code))
                    .findAny()
                    .orElse(null);
            if(tableInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: WeeklyMissionRewardTable not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: WeeklyMissionRewardTable not find", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(tableInfo.getNeedPoint() > missionsDataDto.weeklyRewardPoint) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_GETTING_BONUS_REWARD.getIntegerValue(), "Fail! -> Cause: Not yet getting bonus reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not yet getting bonus reward", ResponseErrorCode.NOT_YET_GETTING_BONUS_REWARD);
            }
            receiveReward(userId, tableInfo.getGettingItem(), tableInfo.getRewardCount(), changedBelongingInventoryList, map, "주간미션 "+split[2]);
            if(tableInfo.getGettingItem().equals("gold")) {
                /* 패스 업적 : 골드획득*/
                changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", tableInfo.getRewardCount(), gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            }
            rewardInfoData.rewardReceived = true;
        }

        String json_missionData = JsonStringHerlper.WriteValueAsStringFromData(missionsDataDto);
        myMissionsData.ResetSaveDataValue(json_missionData);
        map.put("changedBelongingInventoryList", changedBelongingInventoryList);
        map.put("missionsDataDto", missionsDataDto);
        map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
        map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());

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

}