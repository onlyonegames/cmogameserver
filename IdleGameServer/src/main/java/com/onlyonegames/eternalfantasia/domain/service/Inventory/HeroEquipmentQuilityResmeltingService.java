package com.onlyonegames.eternalfantasia.domain.service.Inventory;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
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
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class HeroEquipmentQuilityResmeltingService {
    private final UserRepository userRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyEquipmentDeckRepository myEquipmentDeckRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    //품질재련에 필요한 재련석량 및 골드 비용
    //재련석 수량 공식
    int getNeedResmeltingStone(String grade) {
        int gradeValue = EquipmentCalculate.GradeValue(grade);
        return (int)(10 + ((10 * ((gradeValue - 1) * 0.2)) * (gradeValue - 1)));
    }
    //품질 재련시 필요 골드 수량 공식
    int getNeedResmeltingGold(String grade, String classString) {
        int classValue = 0;
        switch (classString) {
            case "SS":
                classValue = 6;
                break;
            case "S":
                classValue = 5;
                break;
            case "A":
                classValue = 4;
                break;
            case "B":
                classValue = 3;
                break;
            case "C":
                classValue = 2;
                break;
            case "D":
                classValue = 1;
                break;
        }
        int gradeValue = EquipmentCalculate.GradeValue(grade);
        return (5000+ (5000 * ((gradeValue - 1)))) * (gradeValue + ((classValue - 1) * 5));
    }
    public Map<String, Object> QuilityResmelting(Long userId, ItemRequestDto originalItem, ItemRequestDto equipmentMaterial, ItemRequestDto resmeltingMaterial, Map<String, Object> map) {

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

        //해당 장비가 품질 제련이 가능한지 체크(이미 최상위 품질인 아이템은 제련 불가)
        if(heroEquipment.getItemClass().equals("SSS")) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_QUILITYRESMELTING.getIntegerValue(), "Fail! -> Cause: Can't More quilityResmelting.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't More quilityResmelting.", ResponseErrorCode.CANT_MORE_QUILITYRESMELTING);
        }

        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        int itemId = heroEquipment.getItem_Id();
        HeroEquipmentsTable equipmentsTable = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == itemId)
                .findAny()
                .orElse(null);
        if(equipmentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 userId의 정보
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //골드 체크
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getGold();
        int needResmeltingGold = getNeedResmeltingGold(equipmentsTable.getGrade(), heroEquipment.getItemClass());
        if(!user.SpendGold(needResmeltingGold)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Need more resmeltingGold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more resmeltingGold.", ResponseErrorCode.NEED_MORE_GOLD);
        }
        currencyLogDto.setCurrencyLogDto("품질재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]", "골드", previousValue, -needResmeltingGold, user.getGold());
        String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, currencyLog);

        //재료가 최소 한가지 이상 선택되었는지 체크
        if(equipmentMaterial.getId() == 0 || resmeltingMaterial.getId() == 0) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_SELECT_QUILITYRESMELTINGMATERIAL.getIntegerValue(), "Fail! -> Cause: Don't select quilityResmelting material.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Don't select quilityResmelting material.", ResponseErrorCode.DONT_SELECT_QUILITYRESMELTINGMATERIAL);
        }

        MyEquipmentDeck myEquipmentDeck = myEquipmentDeckRepository.findByUserIdUser(userId)
                .orElse(null);
        if(myEquipmentDeck == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyEquipmentDeck not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyEquipmentDeck not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        HeroEquipmentInventory firstMaterialItem = null;
        String equipmentGrade = equipmentsTable.getGrade();

        //첫번째 장비 재료 체크
        firstMaterialItem = userHeroEquipmentInventory.stream()
                .filter(a -> a.getId().equals(equipmentMaterial.getId()))
                .findAny()
                .orElse(null);
        if(firstMaterialItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        int materialId = firstMaterialItem.getItem_Id();
        HeroEquipmentsTable equipmentMaterialInfo = heroEquipmentsTableList.stream()
                .filter(a -> a.getId() == materialId)
                .findAny()
                .orElse(null);
        if(equipmentMaterialInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Original Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Original Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //동일한 등급의 장비가 아니면 재료로 사용 불가
        if(!equipmentGrade.equals(equipmentMaterialInfo.getGrade())) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Cant use materail.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant use materail.", ResponseErrorCode.CANT_USE_MATERIAL);
        }
        //동일한 품질 장비가 아니면 재료로 사용 불가
        if(!heroEquipment.getItemClass().equals(firstMaterialItem.getItemClass())) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Cant use materail.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant use materail.", ResponseErrorCode.CANT_USE_MATERIAL);
        }
        //장착된 장비를 재료로 사용한 경우
        if(myEquipmentDeck.IsIncludeDeckItem(firstMaterialItem.getId()))/*덱에 장착되어 있는경우 다른 장비의 제련 재료로 사용될수 없음*/ {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: material is can't included deck.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: material is can't included deck.", ResponseErrorCode.CANT_USE_MATERIAL);
        }

        //두번째 재련석 재료 체크
        BelongingInventory secondMaterialItem = null;
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        secondMaterialItem = belongingInventoryList.stream()
                .filter(a -> a.getId().equals(resmeltingMaterial.getId()) && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                .findAny()
                .orElse(null);
        if(secondMaterialItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingInventory Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: BelongingInventory Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        int materialItemID = secondMaterialItem.getItemId();
        SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                .filter(a -> a.getId() == materialItemID)
                .findAny()
                .orElse(null);
        if(spendableItemInfoTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItem Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItem Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        /*잘못된 아이템*/
        if(!spendableItemInfoTable.getSpendableType().equals("MATERIAL_FOR_RESMELTING")) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: SpendableItem Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItem Item Can't find.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        List<QuilityResmeltingInfoTable> quilityResmeltingInfoTableList = gameDataTableService.QuilityResmeltingInfoTableList();

        //필요한 재련석 체크
        //String afterQuility = EquipmentCalculate.GetClassCategoryAfterQuilityResmelting(heroEquipment.getItemClass());
        QuilityResmeltingInfoTable quilityResmeltingInfoTable = quilityResmeltingInfoTableList.stream()
                .filter(a -> a.getClassStr().equals(heroEquipment.getItemClass()) && a.getGrade().equals(equipmentGrade))
                .findAny()
                .orElse(null);

        String needResmeltStoneCode = quilityResmeltingInfoTable.getNeedResmeltStone();
        /*재련할 품질과 등급에 따라 필요한 재련석이 다름*/
        if(!needResmeltStoneCode.equals(spendableItemInfoTable.getCode())) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Cant use materail.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant use materail.", ResponseErrorCode.CANT_USE_MATERIAL);
        }


        HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
        String afterItemClass = EquipmentCalculate.GetClassCategoryAfterQuilityResmelting(heroEquipment.getItemClass());
        int afterClassValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(afterItemClass, classValues);
        int gradeValue = EquipmentCalculate.GradeValue(equipmentsTable.getGrade());
        int level = heroEquipment.getLevel();

        double afterDecideDefaultValue = EquipmentCalculate.CalculateEquipmentValue(equipmentsTable.getDefaultAbilityValue(), level, gradeValue, afterClassValue, equipmentsTable.getDefaultGrow(), equipmentsTable.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        double afterDecideSecondValue = EquipmentCalculate.CalculateEquipmentValue(equipmentsTable.getSecondAbilityValue(), level, gradeValue, afterClassValue, equipmentsTable.getSecondGrow(), equipmentsTable.getSECOND_PERCENT_OR_FIXEDVALUE());

        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        String[] options = heroEquipment.getOptionIds().split(",");
        int optionCount = options.length;
        String afterOptionsValues = "";

        for(int i = 0; i < optionCount; i++) {
            String option = options[i];
            if(option == null || option.length() == 0)
                continue;
            int selectedOptionId = Integer.parseInt(option);
            EquipmentOptionsInfoTable equipmentOptionsInfoTable = optionsInfoTableList.stream()
                    .filter(a -> a.getID() == selectedOptionId)
                    .findAny()
                    .orElse(null);
           if(equipmentOptionsInfoTable == null) {
               errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SelectedOptionId Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
               throw new MyCustomException("Fail! -> Cause: SelectedOptionId Can't find.", ResponseErrorCode.NOT_FIND_DATA);
           }

            StringMaker.Clear();
            StringMaker.stringBuilder.append(afterOptionsValues);
            if (afterOptionsValues.length() > 0)
                StringMaker.stringBuilder.append(",");
            double afterOptionValue = EquipmentCalculate.CalculateOptionValue(equipmentOptionsInfoTable.getBaseValue(), level, gradeValue, afterClassValue, equipmentOptionsInfoTable.getPERCENT_OR_FIXEDVALUE());
            StringMaker.stringBuilder.append(afterOptionValue);
            afterOptionsValues = StringMaker.stringBuilder.toString();
        }

        heroEquipment.QuilityResmelting(afterDecideDefaultValue, afterDecideSecondValue, afterItemClass, afterClassValue, afterOptionsValues);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(firstMaterialItem);
        EquipmentLogDto materialEquipment = new EquipmentLogDto();
        materialEquipment.setEquipmentLogDto("품질재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]", firstMaterialItem.getId(), "제거", heroEquipmentInventoryDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(materialEquipment);
        loggingService.setLogging(userId, 2, equipmentLog);
        //장비 테이블에서 삭제
        heroEquipmentInventoryRepository.delete(firstMaterialItem);

        //재련석 소모량 적용
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        belongingInventoryLogDto.setPreviousValue(secondMaterialItem.getCount());
        int needResmeltStoneCount = getNeedResmeltingStone(equipmentGrade);
        if(!secondMaterialItem.SpendItem(needResmeltStoneCount)){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more ResmeltStoneCount count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw  new MyCustomException("Fail! -> Cause: Need more ResmeltStoneCount count", ResponseErrorCode.NEED_MORE_MATERIAL);
        }
        belongingInventoryLogDto.setBelongingInventoryLogDto("품질재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]", secondMaterialItem.getId(), secondMaterialItem.getItemId(), secondMaterialItem.getItemType(), -needResmeltStoneCount, secondMaterialItem.getCount());
        String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
        loggingService.setLogging(userId, 3, belongingLog);
        BelongingInventoryDto secondMaterialItemDto = new BelongingInventoryDto();
        secondMaterialItemDto.InitFromDbData(secondMaterialItem);
        HeroEquipmentInventoryDto heroEquipmentDto = new HeroEquipmentInventoryDto();
        heroEquipmentDto.InitFromDbData(heroEquipment);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto("품질재련 - "+equipmentsTable.getName()+" ["+heroEquipment.getId()+"]", firstMaterialItem.getId(), "변경", heroEquipmentDto);
        String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(userId, 2, log);

        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA));
        if(myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: HeroEquipment Item Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: HeroEquipment Item Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        /* 업적 : 품질 재련 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipment.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        map.put("secondMaterialItem", secondMaterialItemDto);
        map.put("item",heroEquipmentDto);
        map.put("user", user);
        return map;
    }
}
