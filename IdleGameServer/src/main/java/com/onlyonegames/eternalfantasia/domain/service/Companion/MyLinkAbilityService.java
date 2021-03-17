package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.BelongingCharacterPieceTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.eternalfantasia.etc.LinkAbilityCostCalculator;
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
public class MyLinkAbilityService {
    private final MyCharactersRepository myCharactersRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> LinkAbilityLevelUp(Long userId, Long characterId, Map<String, Object> map){

        MyCharacters myCharacter = myCharactersRepository.findById(characterId)
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myCharacters not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myCharacters not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }


        List<herostable> herostableList = gameDataTableService.HerosTableList();

        herostable herostable = herostableList.stream().filter(a -> a.getCode().equals(myCharacter.getCodeHerostable())).findAny().orElse(null);

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);

        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList =  gameDataTableService.BelongingCharacterPieceTableList();
        BelongingCharacterPieceTable belongingCharacterPieceTable = null;
        if(herostable.getTier() == 1) {
            belongingCharacterPieceTable = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().equals("characterPiece_cr_common"))
                    .findAny()
                    .orElse(null);
        }
        else {
            belongingCharacterPieceTable = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(myCharacter.getCodeHerostable()))
                    .findAny()
                    .orElse(null);
        }

        if(belongingCharacterPieceTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: belongingCharacterPieceTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: belongingCharacterPieceTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        int belongingCharacterPieceTableId =  belongingCharacterPieceTable.getId();
        BelongingInventory characterPiece = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName().equals(ItemTypeName.BelongingItem_CharacterPiece) && a.getItemId() == belongingCharacterPieceTableId)
                .findAny()
                .orElse(null);
        if(characterPiece == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more characterPiece", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more characterPiece", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
        }

        // 현재 조각 갯수(링크 어빌리티 경험치)가 다음번 레벨업까지 필요한 갯수보다 모자라거나, 이미 최대 레벨을 찍었으면 실패
        int characterPieceCount = characterPiece.getCount();
        int nowLinkAbilityLevel = myCharacter.getLinkAbilityLevel();
        int cost = LinkAbilityCostCalculator.GetNextLinkAbilityCost(nowLinkAbilityLevel);
        if(!myCharacter.LevelUpLinkAbility()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_LEVELUP_LINKABILITY.getIntegerValue(), "Fail! -> Cause: Cant levelup LinkAbility.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant levelup LinkAbility.", ResponseErrorCode.CANT_LEVELUP_LINKABILITY);
        }

        BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
        belongingInventoryLogDto.setPreviousValue(characterPieceCount);

        if(!characterPiece.SpendItem(cost)){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need More CharacterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need More CharacterPiece.", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
        }

        belongingInventoryLogDto.setBelongingInventoryLogDto(herostable.getName()+" 링크어빌리티 업", characterPiece.getId(), characterPiece.getItemId(), characterPiece.getItemType(), -cost, characterPiece.getCount());
        String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
        loggingService.setLogging(userId, 3, log);
        BelongingInventoryDto characterPieceDto = new BelongingInventoryDto();
        characterPieceDto.InitFromDbData(characterPiece);
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacter);
        map.put("remainCharacterPiece", characterPieceDto);
        map.put("myCharacter", myCharactersBaseDto);

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
        /* 업적 : 링크 어빌리티 업 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVELUP_LINKABILITY.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
        /* 업적 : 링크 어빌리티 각 단계 달성 미션 체크*/
        StringMaker.Clear();
        StringMaker.stringBuilder.append(myCharacter.getLinkAbilityLevel());
        StringMaker.stringBuilder.append("단계");
        String linkAbilityParam = StringMaker.stringBuilder.toString();

        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVELUP_LINKABILITY.name(),linkAbilityParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
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
        /* 패스 업적 : 링크 어빌리티 업 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVELUP_LINKABILITY.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

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
