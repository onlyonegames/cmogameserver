package com.onlyonegames.eternalfantasia.domain.service.Gotcha;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Gotcha.GotchaMileageDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.EquipmentLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GoodsItemDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.GotchaMileage;
import com.onlyonegames.eternalfantasia.domain.model.entity.Gotcha.PickupTable;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.GotchaMileageRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Gotcha.PickupTableRepository;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class GotchaService {

    enum NORMAL_OR_PICKUP {
        NORMAL,
        PICKUP
    }
    private final GotchaMileageRepository gotchaMileageRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final PickupTableRepository pickupTableRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    /** 픽업 리스트*/
    public Map<String, Object> GetPickupEventList(Long userId, Map<String, Object> map) {
        LocalDateTime now = LocalDateTime.now();
        List<PickupTable> pickupTableList = pickupTableRepository.findAllByStartDateBeforeAndEndDateAfter(now, now);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.CheckRechargingTimeFreeGotchaTicket();
        map.put("user", user);
        map.put("pickupTableList", pickupTableList);
        return map;
    }

    /** 인연의 기도 (동료 뽑기) */
    GotchaCharacterTable GotchaCharacterStep1(NORMAL_OR_PICKUP normalOrPickup) {
        List<GotchaCharacterTable> gotchaCharacterTableList = gameDataTableService.GotchaCharacterTableList();
        List<Double> probabilityList = new ArrayList<>();
        for(GotchaCharacterTable gotchaCharacterTable : gotchaCharacterTableList) {
            if(normalOrPickup == NORMAL_OR_PICKUP.NORMAL)
                probabilityList.add((double)gotchaCharacterTable.getNormalGotcha());
            else if(normalOrPickup == NORMAL_OR_PICKUP.PICKUP)
                probabilityList.add((double)gotchaCharacterTable.getPickupGotcha());
        }
        int selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityList);
        return gotchaCharacterTableList.get(selectedIndex);
    }

    GotchaCharacterResponseDto.GotchaCharacterInfo GotchaCharacterStep2(Long pickupTableId, GotchaCharacterResponseDto gotchaCharacterResponseDto, Long userId, List<herostable> herostableList, List<MyCharacters> myCharactersList, List<BelongingCharacterPieceTable> belongingCharacterPieceTableList, List<CharacterToPieceTable> characterToPieceTableList, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList, int limitSameCharacter, Map<String, Integer> previousResultSet, boolean needMinimumTier2, String workingPosition) {
        GotchaCharacterTable gotchaCharacterTable;
        String gotchaCharacterTierCode = "";
        if(needMinimumTier2) {
            List<Double> probabilityList = new ArrayList<>();
            probabilityList.add(0.98);
            probabilityList.add(0.02);
            int selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityList);
            if(selectedIndex == 0)
                gotchaCharacterTierCode = "tier2Character";
            else
                gotchaCharacterTierCode = "tier3Character";
        }
        else {
            if(pickupTableId == 0)
                gotchaCharacterTable = GotchaCharacterStep1(NORMAL_OR_PICKUP.NORMAL);
            else
                gotchaCharacterTable = GotchaCharacterStep1(NORMAL_OR_PICKUP.PICKUP);
            gotchaCharacterTierCode = gotchaCharacterTable.getCode();
        }


        List<herostable> probabilityHerosList = new ArrayList<>();
        switch (gotchaCharacterTierCode) {
            case "tier1Character":
                for(herostable hero : herostableList) {
                    if(hero.getCode().equals("hero"))
                        continue;
                    if(previousResultSet != null) {
                        Integer previousCount = previousResultSet.get(hero.getCode());
                        if(previousCount != null && previousCount >= limitSameCharacter ){
                            continue;
                        }
                    }
                    if(hero.getTier() == 1) {
                        probabilityHerosList.add(hero);
                    }
                }
                break;
            case "tier2Character":
                for(herostable hero : herostableList) {
                    if(hero.getCode().equals("hero"))
                        continue;
                    if(previousResultSet != null) {
                        Integer previousCount = previousResultSet.get(hero.getCode());
                        if (previousCount != null && previousCount >= limitSameCharacter) {
                            continue;
                        }
                    }
                    if(hero.getTier() == 2) {
                        probabilityHerosList.add(hero);
                    }
                }
                break;
            case "tier3Character":
                for(herostable hero : herostableList) {
                    if(hero.getCode().equals("hero"))
                        continue;
                    if(previousResultSet != null) {
                        Integer previousCount = previousResultSet.get(hero.getCode());
                        if (previousCount != null && previousCount >= limitSameCharacter) {
                            continue;
                        }
                    }
                    if(hero.getTier() == 3) {
                        probabilityHerosList.add(hero);
                    }
                }
                break;
            case "pickupCharacter":
                PickupTable pickupTableList = pickupTableRepository.findById(pickupTableId).orElse(null);
                if(pickupTableList == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: pickupTableList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: pickupTableList not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                String pickupCharacterCode = pickupTableList.getPickupCharacterCode();
                Integer previousCount = null;
                if(previousResultSet != null) {
                    previousCount = previousResultSet.get(pickupCharacterCode);
                }
                if(previousCount != null && previousCount >= limitSameCharacter ){
                    for(herostable hero : herostableList) {
                        if(hero.getCode().equals("hero"))
                            continue;
                        previousCount = previousResultSet.get(hero.getCode());
                        if(previousCount != null && previousCount >= limitSameCharacter ){
                            continue;
                        }
                        if(hero.getTier() == 3) {
                            probabilityHerosList.add(hero);
                        }
                    }
                }
                else{
                    herostable probabilityHero = herostableList.stream().filter(a -> a.getCode().equals(pickupCharacterCode)).findAny().orElse(null);
                    if(probabilityHero == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herostableList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: herostableList not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    probabilityHerosList.add(probabilityHero);
                }
                break;
        }
        double randIndex = MathHelper.Range(0, probabilityHerosList.size());
        herostable herostable = probabilityHerosList.get((int)randIndex);
        MyCharacters finalSelectedMyCharacter = myCharactersList.stream().filter(a -> a.getCodeHerostable().equals(herostable.getCode()))
                .findAny()
                .orElse(null);
        if(finalSelectedMyCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: finalSelectedMyCharacter not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: finalSelectedMyCharacter not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo = null;
        if(finalSelectedMyCharacter.isGotcha()) {
            String willFindCharacterCode = finalSelectedMyCharacter.getCodeHerostable();
            if(herostable.getTier() == 1) {
                willFindCharacterCode = "characterPiece_cr_common";
            }
            String finalWillFindCharacterCode = willFindCharacterCode;
            BelongingCharacterPieceTable finalCharacterPieceTable = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(finalWillFindCharacterCode))
                    .findAny()
                    .orElse(null);
            if(finalCharacterPieceTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: characterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(a -> a.getId() == herostable.getTier()).findAny().orElse(null);
            if(characterToPieceTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: characterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            BelongingInventoryDto belongingInventoryDto = AddSelectedCharacterPiece(userId, characterToPieceTable.getPieceCount(), itemTypeList, finalCharacterPieceTable, belongingInventoryList, workingPosition);

            gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(finalSelectedMyCharacter.getCodeHerostable(), belongingInventoryDto, null);
            gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
        }
        else{
            finalSelectedMyCharacter.Gotcha();

            gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(finalSelectedMyCharacter.getCodeHerostable(), null, finalSelectedMyCharacter);
            gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
        }
        return gotchaCharacterInfo;
    }

    /** 인연의 기도 무료 */
    public Map<String, Object> FreeGotchaCharacter(Long userId, Long pickupGotchaId, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(!user.SpendFreeGotchaTicketCountPerDay(1)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_FREE_GOTCHA.getIntegerValue(), "Fail! -> Cause: already free gotcha", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: already free gotcha", ResponseErrorCode.ALREADY_FREE_GOTCHA);
        }

        map.put("user", user);

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();

        GotchaCharacterStep2(pickupGotchaId, gotchaCharacterResponseDto, userId, herostableList, myCharactersList, belongingCharacterPieceTableList, characterToPieceTableList, itemTypeList, belongingInventoryList, 0, null, false, "인연의 기도 무료");
        map.put("gotchaCharacterResponseDto", gotchaCharacterResponseDto);

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
        /* 업적 : 선율의 문 사용 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /* 업적 : 새로운 동료 획득 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GOTCHA_NEW_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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
        /* 패스 업적 : 선율의 문 사용 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
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
    /** 인연의 기도 1회*/
    public Map<String, Object> GotchaCharacter(Long userId, Long pickupGotchaId, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        boolean spentCost = false;
        BelongingInventory dimensionStone = belongingInventoryList.stream().filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == 18).findAny().orElse(null);
        List<GotchaTable> gotchaTableList = gameDataTableService.GotchaTableList();
        GotchaTable gotchaTable = gotchaTableList.get(0);
        String gotchaCostStr = gotchaTable.getGotchaCharacterCost1_1();
        String[] costArr = gotchaCostStr.split(":");
        int cost = Integer.parseInt(costArr[1]);
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        if(dimensionStone != null)
            belongingInventoryLogDto.setPreviousValue(dimensionStone.getCount());
        if(dimensionStone != null && dimensionStone.SpendItem(cost)) {
            spentCost = true;
            map.put("spentDimensionStone", dimensionStone);
            belongingInventoryLogDto.setBelongingInventoryLogDto("인연의 기도 1회", dimensionStone.getId(), dimensionStone.getItemId(), dimensionStone.getItemType(), -cost, dimensionStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }
        if(!spentCost) {
            gotchaCostStr = gotchaTable.getGotchaCharacterCost1_2();
            costArr = gotchaCostStr.split(":");
            cost = Integer.parseInt(costArr[1]);
            if(costArr[0].equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                if (!user.SpendDiamond(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setCurrencyLogDto("인연의 기도 1회", "다이아", previousValue, -cost, user.getDiamond());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            else if(costArr[0].equals("gold")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getGold();
                if (!user.SpendGold(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setCurrencyLogDto("인연의 기도 1회", "골드", previousValue, -cost, user.getGold());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            map.put("user", user);
        }
        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: gotchMileage not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: gotchMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        gotchaMileage.AddMileage(1);
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();

        GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();

        GotchaCharacterStep2(pickupGotchaId, gotchaCharacterResponseDto, userId, herostableList, myCharactersList, belongingCharacterPieceTableList, characterToPieceTableList, itemTypeList, belongingInventoryList, 0, null, false, "인연의 기도 1회");
        map.put("gotchaCharacterResponseDto", gotchaCharacterResponseDto);
        map.put("mileage", gotchaMileageDto);

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
        /* 업적 : 선율의 문 사용 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /* 업적 : 새로운 동료 획득 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GOTCHA_NEW_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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
        /* 패스 업적 : 선율의 문 사용 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
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
    /** 인연의 기도 10회*/
    public Map<String, Object> GotchaCharacter10(Long userId, Long pickupGotchaId, Map<String, Object> map) {

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        boolean spentCost = false;
        BelongingInventory dimensionStone = belongingInventoryList.stream().filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == 18).findAny().orElse(null);
        List<GotchaTable> gotchaTableList = gameDataTableService.GotchaTableList();
        GotchaTable gotchaTable = gotchaTableList.get(0);
        String gotchaCostStr = gotchaTable.getGotchaCharacterCost10_1();
        String[] costArr = gotchaCostStr.split(":");
        int cost = Integer.parseInt(costArr[1]);
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        if(dimensionStone != null)
            belongingInventoryLogDto.setPreviousValue(dimensionStone.getCount());
        if(dimensionStone != null && dimensionStone.SpendItem(cost)) {
            spentCost = true;
            map.put("spentDimensionStone", dimensionStone);
            belongingInventoryLogDto.setBelongingInventoryLogDto("인연의 기도 10회", dimensionStone.getId(), dimensionStone.getItemId(), dimensionStone.getItemType(), -cost, dimensionStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }
        if(!spentCost) {
            gotchaTable = gotchaTableList.get(0);
            gotchaCostStr = gotchaTable.getGotchaCharacterCost10_2();
            costArr = gotchaCostStr.split(":");
            cost = Integer.parseInt(costArr[1]);
            if(costArr[0].equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                if (!user.SpendDiamond(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setCurrencyLogDto("인연의 기도 10회", "다이아", previousValue, -cost, user.getDiamond());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            else if(costArr[0].equals("gold")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getGold();
                if (!user.SpendGold(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setCurrencyLogDto("인연의 기도 10회", "골드", previousValue, -cost, user.getGold());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            map.put("user", user);
        }

        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: gotchMileage not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: gotchMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        gotchaMileage.AddMileage(10);
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        Map<String, Integer> resultGotchaList = new HashMap<>();

        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();

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

        boolean gotchaMinimumTier2 = false;
        for(int i = 0; i < 10; i++) {
            GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo;
            if(i == 9 && !gotchaMinimumTier2)
                gotchaCharacterInfo = GotchaCharacterStep2(pickupGotchaId, gotchaCharacterResponseDto, userId, herostableList, myCharactersList, belongingCharacterPieceTableList, characterToPieceTableList, itemTypeList, belongingInventoryList, 1, resultGotchaList, true, "인연의 기도 10회");
            else
                gotchaCharacterInfo = GotchaCharacterStep2(pickupGotchaId, gotchaCharacterResponseDto, userId, herostableList, myCharactersList, belongingCharacterPieceTableList, characterToPieceTableList, itemTypeList, belongingInventoryList, 1, resultGotchaList, false, "인연의 기도 10회");
            Integer count = resultGotchaList.get(gotchaCharacterInfo.characterCode);
            if(count == null){
                count = 0;
            }
            count++;
            resultGotchaList.put(gotchaCharacterInfo.characterCode, count);
            if(!gotchaMinimumTier2) {
                herostable herostable = herostableList.stream().filter(a -> a.getCode().equals(gotchaCharacterInfo.characterCode)).findAny().orElse(null);
                if(herostable.getTier() > 1) {
                    gotchaMinimumTier2 = true;
                }
            }
            /* 업적 : 선율의 문 사용 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 패스 업적 : 선율의 문 사용 미션 체크*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            /* 업적 : 새로운 동료 획득 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GOTCHA_NEW_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        }
        map.put("gotchaCharacterResponseDto", gotchaCharacterResponseDto);
        map.put("mileage", gotchaMileageDto);

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
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

    GotchaEtcTable GotchaEtcStep1() {
        List<GotchaEtcTable> gotchaEtcTableList = gameDataTableService.GotchaEtcTableList();
        List<Double> probabilityList = new ArrayList<>();
        for(GotchaEtcTable gotchaEtcTable : gotchaEtcTableList) {
            probabilityList.add((double)gotchaEtcTable.getChance());
        }
        int selectedIndex = MathHelper.RandomIndexWidthProbability(probabilityList);
        return gotchaEtcTableList.get(selectedIndex);
    }

    /** 선율의 기도 잡다한거 뽑기*/
    public Map<String, Object> GotchaEtc(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        boolean spentCost = false;
        BelongingInventory dimensionStone = belongingInventoryList.stream().filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == 18).findAny().orElse(null);
        List<GotchaTable> gotchaTableList = gameDataTableService.GotchaTableList();
        GotchaTable gotchaTable = gotchaTableList.get(0);
        String gotchaCostStr = gotchaTable.getGotchaEtcCost1_1();
        String[] costArr = gotchaCostStr.split(":");
        int cost = Integer.parseInt(costArr[1]);
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        if(dimensionStone != null)
            belongingInventoryLogDto.setPreviousValue(dimensionStone.getCount());
        if(dimensionStone != null && dimensionStone.SpendItem(cost)) {
            spentCost = true;
            map.put("spentDimensionStone", dimensionStone);
            belongingInventoryLogDto.setBelongingInventoryLogDto("선율의 기도 1회", dimensionStone.getId(), dimensionStone.getItemId(), dimensionStone.getItemType(), -cost, dimensionStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }
        if(!spentCost) {
            gotchaCostStr = gotchaTable.getGotchaEtcCost1_2();
            costArr = gotchaCostStr.split(":");
            cost = Integer.parseInt(costArr[1]);
            if(costArr[0].equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                if (!user.SpendDiamond(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setCurrencyLogDto("선율의 기도 1회", "다이아", previousValue, -cost, user.getDiamond());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            else if(costArr[0].equals("gold")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getGold();
                if (!user.SpendGold(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setCurrencyLogDto("선율의 기도 1회", "골드", previousValue, -cost, user.getGold());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
        }

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

        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: gotchMileage not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: gotchMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        gotchaMileage.AddMileage(1);

        //장비, 인장, 아레나 티켓, 열쇠, 링크 포인트, 즉시 제작권, 강화석, 재련석
        GotchaEtcTable gotchaEtcTable = GotchaEtcStep1();

        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();

        GotchaResponseDto gotchaResponseDto = new GotchaResponseDto();
        BelongingInventoryDto belongingInventoryDto;

        if(gotchaEtcTable.getCode().contains("equipmentItem_")){
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = AddEquipmentItem(user, gotchaEtcTable.getCode(), "선율의 기도 1회");
            gotchaResponseDto.getChangedHeroEquipmentInventoryList().add(heroEquipmentInventoryDto);

            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
            HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                    .filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id())
                    .findAny()
                    .orElse(null);

            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), heroEquipmentsTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        }
        else if(gotchaEtcTable.getCode().contains("stamp")) {
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = AddPassiveItem(user, gotchaEtcTable.getCode(), "선율의 기도 1회");
            gotchaResponseDto.getChangedHeroEquipmentInventoryList().add(heroEquipmentInventoryDto);
        }
        else if(gotchaEtcTable.getCode().contains("arenaTicket")) {
            int gettingCount = gotchaEtcTable.getGettingCount();
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getArenaTicket();
            user.AddArenaTicket(gettingCount);
            currencyLogDto.setCurrencyLogDto("선율의 기도 1회", "아레나 티켓", previousValue, gettingCount, user.getArenaTicket());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            GoodsItemDto goodsItemDto = new GoodsItemDto("arenaTicket", gettingCount);
            gotchaResponseDto.getChangedGoodsList().add(goodsItemDto);
        }
        else if(gotchaEtcTable.getCode().contains("linkweapon_")) {
            belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 1회");
            gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
        }
        else if(gotchaEtcTable.getCode().contains("linkPoint_")) {
            int gettingCount = gotchaEtcTable.getGettingCount();
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getLinkforcePoint();
            user.AddLinkforcePoint(gettingCount);
            currencyLogDto.setCurrencyLogDto("선율의 기도 1회", "링크 포인트", previousValue, gettingCount, user.getLinkforcePoint());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            GoodsItemDto goodsItemDto = new GoodsItemDto("linkPoint", gettingCount);
            gotchaResponseDto.getChangedGoodsList().add(goodsItemDto);

        }
        else if(gotchaEtcTable.getCode().contains("ticket_direct_production_")) {
            belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 1회");
            gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
        }
        else if(gotchaEtcTable.getCode().contains("enchant_")) {
            belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 1회");
            gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
        }
        else if(gotchaEtcTable.getCode().contains("resmelt")) {
            belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 1회");
            gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
        }

        /* 업적 : 선율의 문 사용 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 패스 업적 : 선율의 문 사용 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);
        map.put("user", user);
        map.put("gotchaResponse", gotchaResponseDto);
        map.put("mileage", gotchaMileageDto);
        return map;
    }

    public Map<String, Object> GotchaEtc10(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        boolean spentCost = false;
        BelongingInventory dimensionStone = belongingInventoryList.stream().filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == 18).findAny().orElse(null);
        List<GotchaTable> gotchaTableList = gameDataTableService.GotchaTableList();
        GotchaTable gotchaTable = gotchaTableList.get(0);
        String gotchaCostStr = gotchaTable.getGotchaEtcCost10_1();
        String[] costArr = gotchaCostStr.split(":");
        int cost = Integer.parseInt(costArr[1]);
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        if(dimensionStone != null)
            belongingInventoryLogDto.setPreviousValue(dimensionStone.getCount());
        if(dimensionStone != null && dimensionStone.SpendItem(cost)) {
            spentCost = true;
            map.put("spentDimensionStone", dimensionStone);
            belongingInventoryLogDto.setBelongingInventoryLogDto("선율의 기도 10회", dimensionStone.getId(), dimensionStone.getItemId(), dimensionStone.getItemType(), -cost, dimensionStone.getCount());
            String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, log);
        }
        if(!spentCost) {
            gotchaTable = gotchaTableList.get(0);
            gotchaCostStr = gotchaTable.getGotchaEtcCost10_2();
            costArr = gotchaCostStr.split(":");
            cost = Integer.parseInt(costArr[1]);
            if(costArr[0].equals("diamond")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getDiamond();
                if (!user.SpendDiamond(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
                }
                currencyLogDto.setCurrencyLogDto("선율의 기도 10회", "다이아", previousValue, -cost, user.getDiamond());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
            else if(costArr[0].equals("gold")) {
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getGold();
                if (!user.SpendGold(cost)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOLD.getIntegerValue(), "Fail! -> Cause: Need more gold.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more gold.", ResponseErrorCode.NEED_MORE_GOLD);
                }
                currencyLogDto.setCurrencyLogDto("선율의 기도 10회", "골드", previousValue, -cost, user.getGold());
                String currencyLog = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, currencyLog);
            }
        }

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

        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: gotchMileage not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: gotchMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        gotchaMileage.AddMileage(10);

        //장비, 인장, 아레나 티켓, 열쇠, 링크 포인트, 즉시 제작권, 강화석, 재련석

        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();

        GotchaResponseDto gotchaResponseDto = new GotchaResponseDto();
        BelongingInventoryDto belongingInventoryDto;
        for(int i = 0; i < 10; i++) {
            GotchaEtcTable gotchaEtcTable = GotchaEtcStep1();
            if(gotchaEtcTable.getCode().contains("equipmentItem_")){
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = AddEquipmentItem(user, gotchaEtcTable.getCode(), "선율의 기도 10회");
                gotchaResponseDto.getChangedHeroEquipmentInventoryList().add(heroEquipmentInventoryDto);

                /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

                HeroEquipmentsTable heroEquipmentsTable = heroEquipmentsTableList.stream()
                        .filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id())
                        .findAny()
                        .orElse(null);

                /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), heroEquipmentsTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
            else if(gotchaEtcTable.getCode().contains("stamp")) {
                HeroEquipmentInventoryDto heroEquipmentInventoryDto = AddPassiveItem(user, gotchaEtcTable.getCode(), "선율의 기도 10회");
                gotchaResponseDto.getChangedHeroEquipmentInventoryList().add(heroEquipmentInventoryDto);
            }
            else if(gotchaEtcTable.getCode().contains("arenaTicket")) {
                int gettingCount = gotchaEtcTable.getGettingCount();
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getArenaTicket();
                user.AddArenaTicket(gettingCount);
                currencyLogDto.setCurrencyLogDto("선율의 기도 10회", "아레나 티켓", previousValue, gettingCount, user.getArenaTicket());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                GoodsItemDto goodsItemDto = new GoodsItemDto("arenaTicket", gettingCount);
                gotchaResponseDto.getChangedGoodsList().add(goodsItemDto);
            }
            else if(gotchaEtcTable.getCode().contains("linkweapon_")) {
                belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 10회");
                gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
            }
            else if(gotchaEtcTable.getCode().contains("linkPoint_")) {
                int gettingCount = gotchaEtcTable.getGettingCount();
                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
                int previousValue = user.getLinkforcePoint();
                user.AddLinkforcePoint(gettingCount);
                currencyLogDto.setCurrencyLogDto("선율의 기도 10회", "링크 포인트", previousValue, gettingCount, user.getLinkforcePoint());
                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
                loggingService.setLogging(userId, 1, log);
                GoodsItemDto goodsItemDto = new GoodsItemDto("linkPoint", gettingCount);
                gotchaResponseDto.getChangedGoodsList().add(goodsItemDto);

            }
            else if(gotchaEtcTable.getCode().contains("ticket_direct_production_")) {
                belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 10회");
                gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
            }
            else if(gotchaEtcTable.getCode().contains("enchant_")) {
                belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 10회");
                gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
            }
            else if(gotchaEtcTable.getCode().contains("resmelt")) {
                belongingInventoryDto = AddSpendAbleItem(userId, gotchaEtcTable.getCode(), gotchaEtcTable.getGettingCount(), itemTypeList, belongingInventoryList, spendableItemInfoTableList, "선율의 기도 10회");
                gotchaResponseDto.getChangedBelongingInventoryList().add(belongingInventoryDto);
            }

            /* 업적 : 선율의 문 사용 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 패스 업적 : 선율의 문 사용 미션 체크*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.USING_GOTCHA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
        }

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
        }
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);
        map.put("user", user);
        map.put("gotchaResponse", gotchaResponseDto);
        map.put("mileage", gotchaMileageDto);
        return map;
    }

    BelongingInventoryDto AddSpendAbleItem(Long userId, String gettingItemCode, int gettingCount, List<ItemType> itemTypeList, List<BelongingInventory> belongingInventoryList,List<SpendableItemInfoTable> spendableItemInfoTableList, String workingPosition) {
        SpendableItemInfoTable spendAbleItem = spendableItemInfoTableList.stream()
                .filter(a -> a.getCode().equals(gettingItemCode))
                .findAny()
                .orElse(null);
        if(spendAbleItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: SpendableItemInfoTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: SpendableItemInfoTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        BelongingInventory inventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == spendAbleItem.getId())
                .findAny()
                .orElse(null);
        if(inventoryItem != null) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
            inventoryItem.AddItem(gettingCount, spendAbleItem.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), gettingCount, inventoryItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(inventoryItem);
            belongingInventoryDto.setCount(gettingCount);
            return belongingInventoryDto;
        }
        else {
            ItemType spendableItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                    .findAny()
                    .orElse(null);
            if(spendableItemType == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(spendAbleItem.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(spendableItemType);
            BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
            willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, willAddBelongingInventoryItem.getId(), willAddBelongingInventoryItem.getItemId(), willAddBelongingInventoryItem.getItemType(), gettingCount, willAddBelongingInventoryItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryList.add(willAddBelongingInventoryItem);
            belongingInventoryDto.setId(willAddBelongingInventoryItem.getId());
            return belongingInventoryDto;
        }
    }

    BelongingInventoryDto AddSelectedCharacterPiece(Long userId, int gettingCount, List<ItemType> itemTypeList, BelongingCharacterPieceTable characterPieceTable, List<BelongingInventory> belongingInventoryList, String workingPosition) {
        ItemType belongingCharacterPieceItem = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);

        BelongingCharacterPieceTable finalCharacterPieceTable = characterPieceTable;
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == finalCharacterPieceTable.getId())
                .findAny()
                .orElse(null);

        if(myCharacterPieceItem == null) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(0);
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryList.add(myCharacterPieceItem);
        }
        else {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
            myCharacterPieceItem.AddItem(gettingCount, characterPieceTable.getStackLimit());
            belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
            belongingInventoryDto.setId(myCharacterPieceItem.getId());
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(characterPieceTable.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(belongingCharacterPieceItem);
        }
        return belongingInventoryDto;
    }

    HeroEquipmentInventoryDto AddEquipmentItem(User user, String gettingItemCode, String workingPosition) {

        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        String decideGrade = "Normal";
        List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
        if(gettingItemCode.contains("NormalAll")) {
            decideGrade = "Normal";
        }
        else if(gettingItemCode.contains("RareAll")) {
            decideGrade = "Rare";
        }
        else if(gettingItemCode.contains("HeroAll")) {
            decideGrade = "Hero";
        }
        else if(gettingItemCode.contains("LegendAll")) {
            decideGrade = "Legend";
        }
        else if(gettingItemCode.contains("DivineAll")) {
            decideGrade = "Divine";
        }
        else if(gettingItemCode.contains("AncientAll")) {
            decideGrade = "Ancient";
        }
        String itemClass = "C";
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
        HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
        classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);
        List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, decideGrade);
        int randValue = (int)MathHelper.Range(0, probabilityList.size());
        HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
        List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
        HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, itemClass, classValue, optionsInfoTableList);
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto(workingPosition+" - "+selectEquipment.getName()+" ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(user.getId(), 2, equipmentLog);
        return heroEquipmentInventoryDto;
    }

    HeroEquipmentInventoryDto AddPassiveItem(User user, String gettingItemCode, String workingPosition) {

        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());
        if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
        }

        String itemClass = "C";
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
        HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
        classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);
        List<PassiveItemTable> passiveItemTables = gameDataTableService.PassiveItemTableList();
        List<PassiveItemTable> copyPassiveItemTables = new ArrayList<>();
        copyPassiveItemTables.addAll(passiveItemTables);
        List<PassiveItemTable> deleteList = new ArrayList<>();
        boolean deleted = false;
        for(PassiveItemTable passiveItemTable : copyPassiveItemTables){
            String code = passiveItemTable.getCode();
            if(code.equals("passiveItem_00_10")) {
                deleteList.add(passiveItemTable);
                deleted = true;
                break;
            }
        }
        if(deleted)
            copyPassiveItemTables.removeAll(deleteList);

        int selectedIndex  = (int)MathHelper.Range(0, copyPassiveItemTables.size());
        PassiveItemTable selectedPassiveItem = copyPassiveItemTables.get(selectedIndex);
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(user.getId());
        dto.setItem_Id(selectedPassiveItem.getId());
        dto.setItemClassValue(classValue);
        dto.setDecideDefaultAbilityValue(0);
        dto.setDecideSecondAbilityValue(0);
        dto.setLevel(1);
        dto.setMaxLevel(1);
        dto.setExp(0);
        dto.setNextExp(0);
        dto.setItemClass(itemClass);

        HeroEquipmentInventory generatedItem = dto.ToEntity();
        generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
        HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
        heroEquipmentInventoryDto.InitFromDbData(generatedItem);
        EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
        equipmentLogDto.setEquipmentLogDto(workingPosition+" - "+selectedPassiveItem.getName()+" ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
        String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
        loggingService.setLogging(user.getId(), 2, equipmentLog);
        return heroEquipmentInventoryDto;
    }

    public Map<String, Object> GettingHeroPieceFromMileage(Long userId, String selectCharacterCode, Map<String, Object> map) {

        GotchaMileage gotchaMileage = gotchaMileageRepository.findByUseridUser(userId)
                .orElse(null);
        if(gotchaMileage == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: gotchMileage not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: gotchMileage not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(false == gotchaMileage.SpendMileageForHero()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOTCHAMILEAGE.getIntegerValue(), "Fail! -> Cause: need more gotchMileage.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: need more gotchMileage.", ResponseErrorCode.NEED_MORE_GOTCHAMILEAGE);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        MyCharacters finalSelectedMyCharacter = myCharactersList.stream().filter(a -> a.getCodeHerostable().equals(selectCharacterCode))
                .findAny()
                .orElse(null);
        if(finalSelectedMyCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: finalSelectedMyCharacter not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: finalSelectedMyCharacter not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();
        GotchaCharacterResponseDto.GotchaCharacterInfo gotchaCharacterInfo;

        if(finalSelectedMyCharacter.isGotcha()) {
            List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            List<ItemType> itemTypeList = itemTypeRepository.findAll();
            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
            List<CharacterToPieceTable> characterToPieceTableList = gameDataTableService.CharacterToPieceTableList();

            List<herostable> herostableList = gameDataTableService.HerosTableList();
            herostable herostable = herostableList.stream().filter(a -> a.getCode().equals(selectCharacterCode)).findAny().orElse(null);
            if(herostable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: herostableList not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: herostableList not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            String willFindCharacterCode = finalSelectedMyCharacter.getCodeHerostable();
            if(herostable.getTier() == 1) {
                willFindCharacterCode = "characterPiece_cr_common";
            }
            String finalWillFindCharacterCode = willFindCharacterCode;
            BelongingCharacterPieceTable finalCharacterPieceTable = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(finalWillFindCharacterCode))
                    .findAny()
                    .orElse(null);
            if(finalCharacterPieceTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: characterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            CharacterToPieceTable characterToPieceTable = characterToPieceTableList.stream().filter(a -> a.getId() == herostable.getTier()).findAny().orElse(null);
            if(characterToPieceTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: characterToPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: characterToPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventoryDto belongingInventoryDto = AddSelectedCharacterPiece(userId, characterToPieceTable.getPieceCount(), itemTypeList, finalCharacterPieceTable, belongingInventoryList, "마일리지 확정 뽑기");

            gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(finalSelectedMyCharacter.getCodeHerostable(), belongingInventoryDto, null);
            gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
        }
        else{
            finalSelectedMyCharacter.Gotcha();

            gotchaCharacterInfo = new GotchaCharacterResponseDto.GotchaCharacterInfo(finalSelectedMyCharacter.getCodeHerostable(), null, finalSelectedMyCharacter);
            gotchaCharacterResponseDto.getCharacterInfoList().add(gotchaCharacterInfo);
        }

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
        /* 업적 : 새로운 동료 획득 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GOTCHA_NEW_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        map.put("gotchaCharacterResponseDto", gotchaCharacterResponseDto);
        GotchaMileageDto gotchaMileageDto = new GotchaMileageDto();
        gotchaMileageDto.InitFromDbData(gotchaMileage);
        map.put("mileage", gotchaMileageDto);
        return map;
    }
}
