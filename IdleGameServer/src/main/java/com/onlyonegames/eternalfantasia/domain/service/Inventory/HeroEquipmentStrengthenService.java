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
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SpendableItemInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentDeckRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
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
public class HeroEquipmentStrengthenService {

    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> StrengthenEquipment(Long userId, ItemRequestDto originalItem, List<ItemRequestDto> materialItemsList, Map<String, Object> map) {
        //해당 userId의 EquipmentInventory 에 OriginalItem 이 있는지 확인.
        List<HeroEquipmentInventory> userHeroEquipmentInventory = heroEquipmentInventoryRepository.findByUseridUser(userId);
        HeroEquipmentInventory heroEquipment = userHeroEquipmentInventory.stream()
                .filter(a -> a.getId().equals(originalItem.getId()))
                .findAny()
                .orElse(null);
        if(heroEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 EquipmentItem 이 추가 강화가 가능한지 체크.
        if(heroEquipment.getLevel() == heroEquipment.getMaxLevel()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_EQUIPMENT_LVUP.getIntegerValue(), "Fail! -> Cause: Cant More Equipment Lvup.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant More Equipment Lvup.", ResponseErrorCode.CANT_MORE_EQUIPMENT_LVUP);
        }

        //해당 userId의 EquipmentInventory 와 BelongingInventory 리스트 Get
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        //장비가 재료로 사용된경우의 리스트
        List<HeroEquipmentInventory> materialEquipmentInventoryList = new ArrayList<>();
        //강화석을 재료로 사용한경우의 리스트
        List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();

        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        HeroEquipmentsTable originalHeroEquipment = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == heroEquipment.getItem_Id())
                .findAny()
                .orElse(null);

        if(originalHeroEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentsTable Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentsTable Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<Integer> enchantCountList = new ArrayList<>();
        //materialItemsList 순환하면서 아이템 종류에 따라 EquipmentInventory 혹은 BelongingInventory 에 해당 아이템이 있는지 확인하면서
        for(ItemRequestDto item : materialItemsList) {
            String itemCode = item.getCode();
            if(itemCode.contains("enchant")) {
                //강화석을 재료로 사용한 경우
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                BelongingInventory inventoryItem = belongingInventoryList.stream()
                        .filter(a -> a.getId().equals(item.getId()) && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);
                if(inventoryItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: material Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: material Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
                if(!inventoryItem.SpendItem(item.getCount())){
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more Strengthen StoneCount count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw  new MyCustomException("Fail! -> Cause: Need more Strengthen StoneCount count", ResponseErrorCode.NEED_MORE_MATERIAL);
                }
                belongingInventoryLogDto.setBelongingInventoryLogDto("장비강화 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), -item.getCount(), inventoryItem.getCount());
                String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, belongingLog);
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.InitFromDbData(inventoryItem);
                belongingInventoryDtoList.add(belongingInventoryDto);
                enchantCountList.add(item.getCount());
            }
            else if(itemCode.contains("sword")
                    || itemCode.contains("spear")
                    || itemCode.contains("bow")
                    || itemCode.contains("gun")
                    || itemCode.contains("wand")
                    || itemCode.contains("helmet")
                    || itemCode.contains("armor")
                    || itemCode.contains("accessory")) {
                //장비를 재료로 사용한 경우
                if(myEquipmentDeck.IsIncludeDeckItem(item.getId()))/*덱에 장착되어 있는경우 다른 장비의 강화재료로 사용될수 없음*/ {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: material is can't included deck.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: material is can't included deck.", ResponseErrorCode.CANT_USE_MATERIAL);
                }
                HeroEquipmentInventory inventoryItem = userHeroEquipmentInventory.stream()
                        .filter(a -> a.getId().equals(item.getId()))
                        .findAny()
                        .orElse(null);
                if(inventoryItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: material Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: material Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
                }
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
                heroEquipmentInventoryDto.InitFromDbData(inventoryItem);
                EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
                equipmentLogDto.setEquipmentLogDto("장비강화 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", inventoryItem.getId(), "제거", heroEquipmentInventoryDto);
                String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
                loggingService.setLogging(userId, 2, equipmentLog);
                materialEquipmentInventoryList.add(inventoryItem);
            }
            else {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not exist code for equipment material.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not exist code for equipment material.", ResponseErrorCode.NOT_EXIST_CODE);
            }
        }
        //각 아이템마다 코스트 및 경험치 계산후 합산.
        int originalEquipmentGradeValue = EquipmentCalculate.GradeValue(originalHeroEquipment.getGrade());
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        int sumCost = 0;
        int sumExp = 0;
        int maxExp = GetNextLevelUpExp(heroEquipment.getMaxLevel(), originalEquipmentGradeValue, heroEquipment.getMaxLevel());
        int checkIndex = 0;//강화석 동일 종류를 몇개나 넣었는지 체크하기위한 변수
        for(BelongingInventoryDto belongingInventoryDto : belongingInventoryDtoList) {
            SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                    .filter(a -> a.getId() == belongingInventoryDto.getItemId())
                    .findAny()
                    .orElse(null);
            if(spendableItemInfoTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItem Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: SpendableItem Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            /*잘못된 아이템*/
            if(!spendableItemInfoTable.getSpendableType().equals("MATERIAL_FOR_STRENGTHEN")) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: SpendableItem Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: SpendableItem Item Can't find.", ResponseErrorCode.NOT_EXIST_CODE);
            }

            int baseGettingExp = EquipmentCalculate.GetBaseExpFromMaterialGrade(spendableItemInfoTable.getCode(), spendableItemInfoTable.getGrade());
            int willGetExp = WillGetExp(baseGettingExp, EquipmentCalculate.GradeValue(spendableItemInfoTable.getGrade()), 1);
            willGetExp = willGetExp * enchantCountList.get(checkIndex);
            checkIndex++;
            sumCost += CalculateCost(willGetExp, originalEquipmentGradeValue);
            /*강화석은 테이블에 경험치가 이미 정해짐.*/
            sumExp += willGetExp;
        }

        for(HeroEquipmentInventory heroEquipmentInventory : materialEquipmentInventoryList) {
            HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                    .filter(a -> a.getId() == heroEquipmentInventory.getItem_Id())
                    .findAny()
                    .orElse(null);
            if(heroEquipmentsTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipment Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: HeroEquipment Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            //아이템의 종류가 다르면 재료로 사용 불가(무기들의 경우는 종류가 다르더라도 무기이면 재료로 사용 가능
            if(originalHeroEquipment.getKind().equals("Sword") || originalHeroEquipment.getKind().equals("Spear") || originalHeroEquipment.getKind().equals("Bow") ||
                    originalHeroEquipment.getKind().equals("Gun") || originalHeroEquipment.getKind().equals("Wand")) {
                if(!(heroEquipmentsTable.getKind().equals("Sword") || heroEquipmentsTable.getKind().equals("Spear") || heroEquipmentsTable.getKind().equals("Bow") ||
                        heroEquipmentsTable.getKind().equals("Gun") || heroEquipmentsTable.getKind().equals("Wand"))) {

                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Can't use material.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw  new MyCustomException("Fail! -> Cause: Can't use material.", ResponseErrorCode.CANT_USE_MATERIAL);
                }
            }
            else if(!heroEquipmentsTable.getKind().equals(originalHeroEquipment.getKind())) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Can't use material.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't use material.", ResponseErrorCode.CANT_USE_MATERIAL);
            }
            //
            int baseGettingExp = EquipmentCalculate.GetBaseExpFromMaterialGrade(heroEquipmentsTable.getCode(), heroEquipmentsTable.getGrade());
            int willGetExp = WillGetExp(baseGettingExp, EquipmentCalculate.GradeValue(heroEquipmentsTable.getGrade()), heroEquipmentInventory.getLevel());
            sumCost += CalculateCost(willGetExp, originalEquipmentGradeValue);
            sumExp += willGetExp;
        }
        if(sumExp + heroEquipment.getExp() > maxExp){
            sumExp = maxExp - heroEquipment.getExp();
            sumCost = CalculateCost(sumExp, originalEquipmentGradeValue);
        }

        heroEquipmentInventoryRepository.deleteAll(materialEquipmentInventoryList);
        //합산된 코스트와 해당 유저의 골드 비교
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipment Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipment Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getGold();
        if(!user.SpendGold(sumCost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail -> Cause: Need More Gold", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Gold", ResponseErrorCode.NEED_MORE_GOLD);
        }
        currencyLogDto.setCurrencyLogDto("장비강화 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", "골드", previousValue, -sumCost, user.getGold());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);

        //이전 아이템 경험치까지 sumExp
        sumExp += heroEquipment.getExp();
        int willChangeLevel = CalculateLevelFromTotalExp(sumExp, originalEquipmentGradeValue, heroEquipment.getMaxLevel());
        double newDecideDefaultValue = EquipmentCalculate.CalculateEquipmentValue(originalHeroEquipment.getDefaultAbilityValue(), willChangeLevel, originalEquipmentGradeValue, heroEquipment.getItemClassValue(), originalHeroEquipment.getDefaultGrow(), originalHeroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        double newDecideSecondValue = EquipmentCalculate.CalculateEquipmentValue(originalHeroEquipment.getSecondAbilityValue(), willChangeLevel, originalEquipmentGradeValue, heroEquipment.getItemClassValue(), originalHeroEquipment.getSecondGrow(), originalHeroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        int newNextLevelUpExp = GetNextLevelUpExp(willChangeLevel, originalEquipmentGradeValue, heroEquipment.getMaxLevel());
        if(!heroEquipment.Strengthen(willChangeLevel, sumExp, newNextLevelUpExp, newDecideDefaultValue, newDecideSecondValue)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_EQUIPMENT_LVUP.getIntegerValue(), "Fail! -> Cause: Cant More Equipment Lvup.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant More Equipment Lvup.", ResponseErrorCode.CANT_MORE_EQUIPMENT_LVUP);
        }
        HeroEquipmentInventoryDto heroEquipmentDto = new HeroEquipmentInventoryDto();
        heroEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("장비강화 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "변경", heroEquipmentDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
        map.put("exchangeBelongingInventory", belongingInventoryDtoList);
        map.put("item", heroEquipmentDto);
        map.put("user", user);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;
        /* 업적 : 장비 강화 하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STRENGTHEN_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
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
        /* 패스 업적 : 장비 강화 하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STRENGTHEN_EQUIPMENT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

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

    //장비 레벨 따른 획득 경험치 기준
    public static int WillGetExp(int baseGettingExp, int materialGradeValue, int materialLevel) {
        return (int)(baseGettingExp + ((baseGettingExp * (int)materialGradeValue) * (materialLevel - 1)) * (3.5f - ((int)materialGradeValue * 0.2f)));
    }
    //최대 레벨업까지 필요한 골드 량
    public static int CalculateCost(int willGetExp, int grade) {
        return (int)(willGetExp * (10 + (10 * (grade - 1) * 0.5)));
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

    int CalculateLevelFromTotalExp(int totalExp, int grade, int maxLevel) {
        int level = 0;
        int prevExp = 0;
        int maxLoop = maxLevel + 1;
        for (int i = 1; i < maxLoop; i++)
        {
            level = i;
            int nextLevelUpExp = GetNextLevelUpExp(i, grade, maxLevel);
            if (prevExp <= totalExp && totalExp < nextLevelUpExp)
                break;
            prevExp = nextLevelUpExp;
        }
        return level;
    }
}
