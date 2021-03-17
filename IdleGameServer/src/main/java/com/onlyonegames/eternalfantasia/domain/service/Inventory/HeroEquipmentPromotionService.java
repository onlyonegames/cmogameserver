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
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentMaterialInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentOptionsInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsPromotionTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentPromotionService {

    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    public Map<String, Object> RePromotion(Long userId, ItemRequestDto originalItem, Map<String, Object> map) {
        //장비승급 가능 여부 사전 체크-해당 장비가 실제 인벤토리내에 있는지 확인
        List<HeroEquipmentInventory> userHeroEquipmentInventory = heroEquipmentInventoryRepository.findByUseridUser(userId);
        HeroEquipmentInventory heroEquipment = userHeroEquipmentInventory.stream()
                .filter(a -> a.getId().equals(originalItem.getId()))
                .findAny()
                .orElse(null);
        if(heroEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
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

        //승급테이블에서 해당 장비를 승급하기위해 필요한 재료, 갯수, 필요골드 정보겟
        List<HeroEquipmentsPromotionTable> heroEquipmentsPromotionTableList = gameDataTableService.HeroEquipmentsPromotionTableList();
        String nextGrade = originalHeroEquipment.getGrade();

        HeroEquipmentsPromotionTable heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);

        if(heroEquipmentsPromotionTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentsPromotionTable Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentsPromotionTable Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //다이아가 충분한지 체크
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getDiamond();
        int cost = heroEquipmentsPromotionTable.getNeedRetryDimond();
        if(!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail -> Cause: Need More Gold", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Gold", ResponseErrorCode.NEED_MORE_GOLD);
        }
        currencyLogDto.setCurrencyLogDto("장비승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", "다이아", previousValue, -cost, user.getDiamond());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);

        //승급진행
        List<HeroEquipmentsTable> probabilityList = null;
        String finalNextGrade = nextGrade;
        if(originalHeroEquipment.getKind().equals("Sword")
                || originalHeroEquipment.getKind().equals("Spear")
                || originalHeroEquipment.getKind().equals("Bow")
                || originalHeroEquipment.getKind().equals("Gun")
                || originalHeroEquipment.getKind().equals("Wand")) {
            probabilityList = heroEquipmentsTableList.stream()
                    .filter(a -> (a.getKind().equals("Sword") || a.getKind().equals("Spear") || a.getKind().equals("Bow") || a.getKind().equals("Gun") || a.getKind().equals("Wand"))
                            && a.getGrade().equals(finalNextGrade)
                            && !a.getCode().contains("99") && !a.getCode().contains("97"))
                    .collect(Collectors.toList());
            if(probabilityList.size() == 0) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
        else {
            probabilityList = heroEquipmentsTableList.stream()
                    .filter(a -> a.getKind().equals(originalHeroEquipment.getKind())
                            && a.getGrade().equals(finalNextGrade)
                            && !a.getCode().contains("99") && !a.getCode().contains("97"))
                    .collect(Collectors.toList());
            if(probabilityList.size() == 0) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", ResponseErrorCode.NOT_FIND_DATA);
            }
        }

        int randValue = (int) MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);

        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        HeroEquipmentInventoryDto previousEquipmentDto = new HeroEquipmentInventoryDto();
        previousEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto previousEquipmentLogDto = new EquipmentLogDto();
        previousEquipmentLogDto.setEquipmentLogDto("장비재승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "재승급-제거", previousEquipmentDto);
        String previousEquipmentLog = JsonStringHerlper.WriteValueAsStringFromData(previousEquipmentLogDto);
        loggingService.setLogging(userId, 2, previousEquipmentLog);
        HeroEquipmentInventoryDto finalItemDto = EquipmentCalculate.PromotionEquipment(heroEquipment, selectEquipment, optionsInfoTableList, true);
        heroEquipment.Promotion(finalItemDto.getItem_Id(), finalItemDto.getDecideDefaultAbilityValue(), finalItemDto.getDecideSecondAbilityValue(), finalItemDto.getMaxLevel(), finalItemDto.getNextExp(), finalItemDto.getOptionIds(), finalItemDto.getOptionValues());
        HeroEquipmentInventoryDto heroEquipmentDto = new HeroEquipmentInventoryDto();
        heroEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("장비재승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "재승급-추가", heroEquipmentDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
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
        /* 업적 : 승급하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PROMOTION_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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

        /* 패스 업적 : 승급하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PROMOTION_EQUIPMENT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

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

    public Map<String, Object> Promotion(Long userId, ItemRequestDto originalItem, Map<String, Object> map) {
        //장비승급 가능 여부 사전 체크-해당 장비가 실제 인벤토리내에 있는지 확인
        List<HeroEquipmentInventory> userHeroEquipmentInventory = heroEquipmentInventoryRepository.findByUseridUser(userId);
        HeroEquipmentInventory heroEquipment = userHeroEquipmentInventory.stream()
                .filter(a -> a.getId().equals(originalItem.getId()))
                .findAny()
                .orElse(null);
        if(heroEquipment == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //장비승급 가능 여부 사전 체크-각 등급별 최고레벨 달성한 장비만 승급가능
        if(heroEquipment.getLevel() < heroEquipment.getMaxLevel()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MAXLV_PROMOTION.getIntegerValue(), "Fail! -> Cause: Don't Max Lv.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Don't Max Lv.", ResponseErrorCode.NEED_MAXLV_PROMOTION);
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

        //장비승급 가능 여부 사전 체크-최상위 등급의 장비는 더이상 승급 불가
        if(originalHeroEquipment.getGrade().equals("Ancient")) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_PROMOTION.getIntegerValue(), "Fail! -> Cause: Can't more promotion.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't more promotion.", ResponseErrorCode.CANT_MORE_PROMOTION);
        }

        //승급테이블에서 해당 장비를 승급하기위해 필요한 재료, 갯수, 필요골드 정보겟
        List<HeroEquipmentsPromotionTable> heroEquipmentsPromotionTableList = gameDataTableService.HeroEquipmentsPromotionTableList();
        String originalEquipmentGrade = originalHeroEquipment.getGrade();
        String nextGrade = "";
        HeroEquipmentsPromotionTable heroEquipmentsPromotionTable = null;
        if(originalEquipmentGrade.contains("Normal")) {
            nextGrade = "Rare";
            heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);
        }
        else if(originalEquipmentGrade.contains("Rare")) {
            nextGrade = "Hero";
            heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);
        }
        else if(originalEquipmentGrade.contains("Hero")) {
            nextGrade = "Legend";
            heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);
        }
        else if(originalEquipmentGrade.contains("Legend")||originalEquipmentGrade.contains("legend")) {
            nextGrade = "Divine";
            heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);
        }
        else if(originalEquipmentGrade.contains("Divine")) {
            nextGrade = "Ancient";
            heroEquipmentsPromotionTable = getHeroEquipmentsPromotionTable(nextGrade, originalHeroEquipment, heroEquipmentsPromotionTableList);
        }
        if(heroEquipmentsPromotionTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: heroEquipmentsPromotionTable Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: heroEquipmentsPromotionTable Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //골드가 충분한지 체크
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getGold();
        int cost = heroEquipmentsPromotionTable.getNeedGold();
        if(!user.SpendGold(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail -> Cause: Need More Gold", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Gold", ResponseErrorCode.NEED_MORE_GOLD);
        }
        currencyLogDto.setCurrencyLogDto("장비승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", "골드", previousValue, -cost, user.getGold());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);
        //각 재료들의 갯수가 충분한지 체크
        String[] materials = heroEquipmentsPromotionTable.getMaterials().split(",");
        String[] needCounts = heroEquipmentsPromotionTable.getNeedCounts().split(",");
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        for(int i = 0; i < materials.length; i++) {
            String materialCode = materials[i];
            EquipmentMaterialInfoTable equipmentMaterialInfo = equipmentMaterialInfoTableList.stream()
                    .filter(a -> a.getCode().equals(materialCode))
                    .findAny()
                    .orElse(null);
            if(equipmentMaterialInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: EquipmentmaterialInfo Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: EquipmentmaterialInfo Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
            }
            BelongingInventory belongingInventoryItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemId() == equipmentMaterialInfo.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material)
                    .findAny()
                    .orElse(null);
            if(belongingInventoryItem == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingInventory Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: BelongingInventory Can't Find", ResponseErrorCode.NOT_FIND_DATA);
            }
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(belongingInventoryItem.getCount());
            int needCount = Integer.parseInt(needCounts[i]);
            if(!belongingInventoryItem.SpendItem(needCount)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more BelongingInventoryItem count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Need more BelongingInventoryItem count", ResponseErrorCode.NEED_MORE_MATERIAL);
            }
            belongingInventoryLogDto.setBelongingInventoryLogDto("장비승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", belongingInventoryItem.getId(), belongingInventoryItem.getItemId(), belongingInventoryItem.getItemType(), -needCount, belongingInventoryItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
        }
        //승급진행
        List<HeroEquipmentsTable> probabilityList = null;
        String finalNextGrade = nextGrade;
        if(originalHeroEquipment.getKind().equals("Sword")
                || originalHeroEquipment.getKind().equals("Spear")
                || originalHeroEquipment.getKind().equals("Bow")
                || originalHeroEquipment.getKind().equals("Gun")
                || originalHeroEquipment.getKind().equals("Wand")) {
            probabilityList = heroEquipmentsTableList.stream()
                    .filter(a -> (a.getKind().equals("Sword") || a.getKind().equals("Spear") || a.getKind().equals("Bow") || a.getKind().equals("Gun") || a.getKind().equals("Wand"))
                            && a.getGrade().equals(finalNextGrade)
                            && !a.getCode().contains("99") && !a.getCode().contains("97"))
                    .collect(Collectors.toList());
            if(probabilityList.size() == 0) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
        else {
            probabilityList = heroEquipmentsTableList.stream()
                    .filter(a -> a.getKind().equals(originalHeroEquipment.getKind())
                            && a.getGrade().equals(finalNextGrade)
                            && !a.getCode().contains("99") && !a.getCode().contains("97"))
                    .collect(Collectors.toList());
            if(probabilityList.size() == 0) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find heroEquipmentsTable for Promotion ", ResponseErrorCode.NOT_FIND_DATA);
            }
        }


        int randValue = (int) MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);

        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        HeroEquipmentInventoryDto previousEquipmentDto = new HeroEquipmentInventoryDto();
        previousEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto previousEquipmentLogDto = new EquipmentLogDto();
        previousEquipmentLogDto.setEquipmentLogDto("장비승급 - "+originalHeroEquipment.getName()+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "승급-제거", previousEquipmentDto);
        String previousEquipmentLog = JsonStringHerlper.WriteValueAsStringFromData(previousEquipmentLogDto);
        loggingService.setLogging(userId, 2, previousEquipmentLog);
        HeroEquipmentInventoryDto finalItemDto = EquipmentCalculate.PromotionEquipment(heroEquipment, selectEquipment, optionsInfoTableList, false);
        heroEquipment.Promotion(finalItemDto.getItem_Id(), finalItemDto.getDecideDefaultAbilityValue(), finalItemDto.getDecideSecondAbilityValue(), finalItemDto.getMaxLevel(), finalItemDto.getNextExp(), finalItemDto.getOptionIds(), finalItemDto.getOptionValues());
        HeroEquipmentInventoryDto heroEquipmentDto = new HeroEquipmentInventoryDto();
        heroEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("장비승급 - "+selectEquipment.getName()+" ["+heroEquipment.getId()+"]", heroEquipment.getId(), "승급-추가", heroEquipmentDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, equipmentLog);
        List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();
        for(BelongingInventory temp : belongingInventoryList){
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(temp);
            belongingInventoryDtoList.add(belongingInventoryDto);
        }
        map.put("item", heroEquipmentDto);
        map.put("user", user);
        map.put("belongingInventoryList", belongingInventoryDtoList);
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
        /* 업적 : 승급하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PROMOTION_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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

        /* 패스 업적 : 승급하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PROMOTION_EQUIPMENT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

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

    private HeroEquipmentsPromotionTable getHeroEquipmentsPromotionTable(String nextGrade, HeroEquipmentsTable originalHeroEquipment, List<HeroEquipmentsPromotionTable> heroEquipmentsPromotionTableList) {
        String equipmentItemKind = originalHeroEquipment.getKind();
        HeroEquipmentsPromotionTable heroEquipmentsPromotionTable = null;
        StringMaker.Clear();
        if(equipmentItemKind.equals("Sword")
                || equipmentItemKind.equals("Spear")
                || equipmentItemKind.equals("Bow")
                || equipmentItemKind.equals("Gun")
                || equipmentItemKind.equals("Wand")) {

            StringMaker.stringBuilder.append("Next");
            StringMaker.stringBuilder.append(nextGrade);
            StringMaker.stringBuilder.append("_Weapon");
            String promotionTableCode = StringMaker.stringBuilder.toString();
            heroEquipmentsPromotionTable = heroEquipmentsPromotionTableList.stream()
                    .filter(a -> a.getCode().contains(promotionTableCode))
                    .findAny()
                    .orElse(null);
        }
        else if(equipmentItemKind.equals("Armor")) {

            StringMaker.stringBuilder.append("Next");
            StringMaker.stringBuilder.append(nextGrade);
            StringMaker.stringBuilder.append("_Armor");
            String promotionTableCode = StringMaker.stringBuilder.toString();
            heroEquipmentsPromotionTable = heroEquipmentsPromotionTableList.stream()
                    .filter(a -> a.getCode().contains(promotionTableCode))
                    .findAny()
                    .orElse(null);
        }
        else if(equipmentItemKind.equals("Helmet")) {
            StringMaker.stringBuilder.append("Next");
            StringMaker.stringBuilder.append(nextGrade);
            StringMaker.stringBuilder.append("_Helmet");
            String promotionTableCode = StringMaker.stringBuilder.toString();
            heroEquipmentsPromotionTable = heroEquipmentsPromotionTableList.stream()
                    .filter(a -> a.getCode().contains(promotionTableCode))
                    .findAny()
                    .orElse(null);
        }
        else if(equipmentItemKind.equals("Accessory")) {
            StringMaker.stringBuilder.append("Next");
            StringMaker.stringBuilder.append(nextGrade);
            StringMaker.stringBuilder.append("_Accessory");
            String promotionTableCode = StringMaker.stringBuilder.toString();
            heroEquipmentsPromotionTable = heroEquipmentsPromotionTableList.stream()
                    .filter(a -> a.getCode().contains(promotionTableCode))
                    .findAny()
                    .orElse(null);
        }
        return heroEquipmentsPromotionTable;
    }
}