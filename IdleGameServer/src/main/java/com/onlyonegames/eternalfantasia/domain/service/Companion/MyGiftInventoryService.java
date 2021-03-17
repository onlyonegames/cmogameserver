package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CharacterExpLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.GiftLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData.TavernVisitCompanionInfoDataList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyGiftInventoryService {

    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> GetGiftInventory(Long userId, Map<String, Object> map) {
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myGiftInventory == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myGiftInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myGiftInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(myGiftInventory.getInventoryInfos(), GiftItemDtosList.class);
        map.put("myGiftInventoryInfos", giftItemInventorys.giftItemDtoList);
        return map;
    }

    public Map<String, Object> GivePresentsToHero(Long userId, Long toCharacterId, List<GiftItemDtosList.GiftItemDto> presentsList, Map<String, Object> map){
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        List<herostable> herostableList = gameDataTableService.HerosTableList();
        MyCharacters myCharacters = myCharactersList.stream()
                .filter(a -> a.getId().equals(toCharacterId))
                .findAny()
                .orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }
        herostable myHero = herostableList.stream().filter(i -> i.getCode().equals(myCharacters.getCodeHerostable())).findAny().orElse(null);
        if(myHero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find herostable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find herostable", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myGiftInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myGiftInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String inventoryInfosString = myGiftInventory.getInventoryInfos();
        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);
        int sumExp = 0;
        int totalSpend = 0;
        for(GiftItemDtosList.GiftItemDto presentItemDto : presentsList) {
            boolean checkingInventory = false;
            for(GiftItemDtosList.GiftItemDto inventoryItemDto : giftItemInventorys.giftItemDtoList) {
                if(presentItemDto.code.equals(inventoryItemDto.code)) {
                    GiftLogDto giftLogDto = new GiftLogDto();
                    giftLogDto.setPreviousValue(inventoryItemDto.count);
                    if(!inventoryItemDto.SpendItem(presentItemDto.count)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GIFT.getIntegerValue(), "Fail -> Cause: Need more gift", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail -> Cause: Need more gift", ResponseErrorCode.NEED_MORE_GIFT);
                    }
                    totalSpend += presentItemDto.count;
                    giftLogDto.setGiftLogDto(myHero.getName()+" 레벨 업", inventoryItemDto.code, -presentItemDto.count, inventoryItemDto.count);
                    String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                    loggingService.setLogging(userId, 4, log);
                    GiftTable giftTable = giftTableList.stream()
                            .filter(a -> a.getCode().equals(presentItemDto.code))
                            .findAny()
                            .orElse(null);
                    if(giftTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    sumExp += CalculateGoodFeelingGetCount(myCharacters.getCodeHerostable(), giftTable, presentItemDto.count);

                    checkingInventory = true;
                    break;
                }
            }
            if(!checkingInventory) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftItem", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find GiftItem", ResponseErrorCode.NOT_FIND_DATA);
            }
        }
        int previousLevel = myCharacters.getLevel();
        int previousExp = myCharacters.getExp();
        myCharacters.AddExp(sumExp, myCharacters.getMaxLevel());
        CharacterExpLogDto characterExpLogDto = new CharacterExpLogDto();
        characterExpLogDto.setCharacterExpLogDto(myHero.getName()+" 레벨 업", myCharacters.getCodeHerostable(), previousExp, sumExp, myCharacters.getExp());
        String expLog = JsonStringHerlper.WriteValueAsStringFromData(characterExpLogDto);
        loggingService.setLogging(userId, 8, expLog);
//        if(!myCharacters.AddExp(sumExp))
//            throw new MyCustomException("Fail -> Cause: Can't more up goodFeeling", ResponseErrorCode.CANT_MORE_UP_GOODFEELING);
        int afterLevel = myCharacters.getLevel();


        /*업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(myMissionsData = null);
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

        /* 업적 : 동료에게 선물하기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GIVE_PRESENTS_TO_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 패스 업적 : 동료에게 선물하기 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GIVE_PRESENTS_TO_HERO.name(), "empty", totalSpend, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        if(previousLevel < afterLevel){
            //호감도 상승!! -> 레벨
            /* 업적 : 동료 레벨 달성 미션 체크*/
            for(int i = previousLevel;i<=afterLevel;i++){
                StringMaker.Clear();
                StringMaker.stringBuilder.append(i);
                StringMaker.stringBuilder.append("레벨");
                String paramTemp = StringMaker.stringBuilder.toString();
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STEP_UP_GOODFEELING.name(),paramTemp, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            }
            /* 업적 : 동료 호감도 상승 미션 체크 -> 동료 레벨 업 미션 체크로 바뀜 횟수 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STEP_UP_GOODFEELING.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 패스 업적 : 동료 호감도 상승 미션 체크 -> 동료 레벨 업 미션 체크로 바뀜*/
            changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STEP_UP_GOODFEELING.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            int goodFeelingIndex = afterLevel - 1;
            //String goodFeelingParam = MissionsDataDto.MISSION_PARAM.GOODFEELING.getParams().get(goodFeelingIndex);
            /* 업적 : 동료 특정 호감도 달성 미션 체크*/
            //int loopIndex = 0;
//            for(String goodFeelingParam : MissionsDataDto.MISSION_PARAM.GOODFEELING.getParams()){
//
//                if(goodFeelingIndex < loopIndex){
//                    break;
//                }
//                boolean checkedTrue = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.STEP_UP_GOODFEELING.name(), goodFeelingParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList());
//                if(checkedTrue) {
//                    changedMissionsData = changedMissionsData || checkedTrue;
//                    break;
//                }
//
//                loopIndex++;
//            }
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
        inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
        myGiftInventory.ResetInventoryInfos(inventoryInfosString);
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacters);
        map.put("myGiftInventoryInfos", giftItemInventorys.giftItemDtoList);
        map.put("myCharacter",myCharactersBaseDto);
        return map;
    }

    int CalculateGoodFeelingGetCount(String heroCode, GiftTable giftTable, int count) { //동료 선물 선호도에 따른 경험치
        int goodFeelingForThisHero = 0;
        if(IsIncludeKindCategory(giftTable.getBestGiftTo(), heroCode))
        {
            goodFeelingForThisHero = 100;
        }
        else if(IsIncludeKindCategory(giftTable.getGracefulGiftTo(), heroCode))
        {
            goodFeelingForThisHero = 40;
        }
        else if(IsIncludeKindCategory(giftTable.getGoodGiftTo(), heroCode))
        {
            goodFeelingForThisHero = 25;
        }
        else if(IsIncludeKindCategory(giftTable.getNomalGiftTo(), heroCode))
        {
            goodFeelingForThisHero = 10;
        }
        else if(IsIncludeKindCategory(giftTable.getHateGiftTo(), heroCode))
        {
            goodFeelingForThisHero = 5;
        }
        return goodFeelingForThisHero * count;
    }

    public static boolean IsIncludeKindCategory(String category, String heroCode) {
        if(category.contains(heroCode))
            return true;
        return false;
    }
}
