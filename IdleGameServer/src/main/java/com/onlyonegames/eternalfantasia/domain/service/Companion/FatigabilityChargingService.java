package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CharacterFatigabilityLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CurrencyLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
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
public class FatigabilityChargingService {
    private final MyCharactersRepository myCharactersRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    //광고 보기로 피로도 20% 회복
    public Map<String, Object> ReduceChargingTimeFromAD(Long characterId, Map<String, Object> map) {
        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(characterId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharacters not find characterId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCharacters not find characterId.", ResponseErrorCode.NOT_FIND_DATA);
        }
//        if(!myCharacter.ChargingFatigabilityFromAD((int)(60*60*2.5)))
//            throw new MyCustomException("Fail! -> Cause: Can't charging now.", ResponseErrorCode.CANT_CHARGING_NOW);

        if(!myCharacter.ChargingFatigabilityFromAD()) {
            errorLoggingService.SetErrorLog(characterId, ResponseErrorCode.CANT_CHARGING_NOW.getIntegerValue(), "Fail! -> Cause: Can't charging now.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't charging now.", ResponseErrorCode.CANT_CHARGING_NOW);
        }
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacter);
        map.put("myCharacter", myCharactersBaseDto);
        return map;
    }

    //즉시 회복으로 모든 피로도 100 회복
    public Map<String, Object> FatigabilityFullCharging(Long userId, Long characterId, Map<String, Object> map) {

        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharacters not find characterId =>."+characterId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        herostable myhero = herostableList.stream().filter(i -> i.getCode().equals(myCharacter.getCodeHerostable())).findAny().orElse(null);
        if(myhero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: not find herostable. =>"+characterId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find herostable.", ResponseErrorCode.NOT_FIND_DATA);
        }

        long remainProductionSecond = myCharacter.RemainChargingFatigabilitySecond(60*60*2/*2시간 간격*/);
        int costDiamond = 0;
        if(remainProductionSecond > 0) {
            /*1분당 다이아 소비 10개 -> 1분당 다이아 소비 5개로 수정*/
            /*2021-01-26 by 김현호 10분당 3개 소비로 변경*/
            int costPerMinute = 3;
            costDiamond = (int)(remainProductionSecond / 600) * costPerMinute;
            /*나머지값이 있으면 나머지에 상관없이 1분으로 환산해서 소비 다이아 개수 10개 추가*/
            int remainSecond = (int)(remainProductionSecond % 600);
            if(remainSecond > 0)
                costDiamond += costPerMinute;
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getDiamond();
        if(!user.SpendDiamond(costDiamond)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail! -> Cause: Need more diamond.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more diamond.", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        currencyLogDto.setCurrencyLogDto(myhero.getName()+" 피로도 회복", "다이아", previousValue, -costDiamond, user.getDiamond());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        List<Long> charactersId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
        charactersId.add(characterId);
        previousFatigability.add(myCharacter.getFatigability());
        if(!myCharacter.FatigabilityFullCharging()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_CHARGING_NOW.getIntegerValue(), "Fail! -> Cause: Can't charging now.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't charging now.", ResponseErrorCode.CANT_CHARGING_NOW);
        }
        presentFatigability.add(myCharacter.getFatigability());
        CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
        characterFatigabilityLogDto.setCharacterFatigabilityLogDto(myhero.getName()+" 피로도 회복", charactersId, previousFatigability, presentFatigability);
        String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
        loggingService.setLogging(userId, 5, fatigabilityLog);
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacter);
        map.put("myCharacter", myCharactersBaseDto);
        map.put("user", user);
        return map;
    }

    //물약으로 피로도 50% 회복
    public Map<String, Object> FatigabilityChargingFromPotion(Long userId, Long characterId, Map<String, Object> map) {

        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharacters not find characterId. =>"+characterId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        herostable myhero = herostableList.stream().filter(i -> i.getCode().equals(myCharacter.getCodeHerostable())).findAny().orElse(null);
        if(myhero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: not find herostable. =>"+characterId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find herostable.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //피로도 회복 물약 있는지 체크
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        BelongingInventory inventoryItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == 16)
                .findAny()
                .orElse(null);
        if(inventoryItem == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_HAVE_POSITION.getIntegerValue(), "Fail! -> Cause: Not have position.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not have position.", ResponseErrorCode.NOT_HAVE_POSITION);
        }
        
        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        belongingInventoryLogDto.setPreviousValue(inventoryItem.getCount());
        if(!inventoryItem.SpendItem(1)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_HAVE_POSITION.getIntegerValue(), "Fail! -> Cause: Not have position.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not have position.", ResponseErrorCode.NOT_HAVE_POSITION);
        }
        belongingInventoryLogDto.setBelongingInventoryLogDto(myhero.getName()+" 피로도 50% 회복", inventoryItem.getId(), inventoryItem.getItemId(), inventoryItem.getItemType(), -1, inventoryItem.getCount());
        String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
        loggingService.setLogging(userId, 3, log);
        List<Long> charactersId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
        charactersId.add(characterId);
        previousFatigability.add(myCharacter.getFatigability());
        if(!myCharacter.ChargingFatigabilityFromPosition()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_CHARGING_NOW.getIntegerValue(), "Fail! -> Cause: Can't charging now. =>"+characterId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't charging now.", ResponseErrorCode.CANT_CHARGING_NOW);
        }
        presentFatigability.add(myCharacter.getFatigability());
        CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
        characterFatigabilityLogDto.setCharacterFatigabilityLogDto(myhero.getName()+" 피로도 50% 회복", charactersId, previousFatigability, presentFatigability);
        String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
        loggingService.setLogging(userId, 5, fatigabilityLog);
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacter);
        BelongingInventoryDto inventoryItemDto = new BelongingInventoryDto();
        inventoryItemDto.InitFromDbData(inventoryItem);
        map.put("myCharacter", myCharactersBaseDto);
        map.put("positionInfo", inventoryItemDto);

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

        /* 패스 업적 : 피로도 물약 먹기 횟수 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_FATIGABILITY_PORTION.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

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
