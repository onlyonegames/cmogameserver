package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.GoldCost;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkpointCost;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.MaterialsCost;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.BelongingCharacterPieceTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.LinkweaponTalentsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SpendableItemInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyLinkweaponInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
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
public class MyLinkweaponInfoService {
    private final MyLinkweaponInfoRepository myLinkweaponInfoRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final UserRepository userRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    //강화
    //selectedWeaponRank = 현재 강화하고 있는 웨폰 진화 단계
    public Map<String, Object> Strengthen(Long userId, Long characterId, int selectedWeaponId, Map<String, Object> map){
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<herostable> herostableList = gameDataTableService.HerosTableList();
        herostable myhero = herostableList.stream().filter(i -> i.getCode().equals(myCharacter.getCodeHerostable())).findAny().orElse(null);
        if(myhero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find herostable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find herostable", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 유저의 MyLinkweapon 검색
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream()
                .filter(a -> a.ownerCode.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not find companionweaponInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find companionweaponInfo.", ResponseErrorCode.NOT_EXIST_CODE);
        }
        /* 이전에 강화 하고 있던 웨폰 아이디가 맞는지 체크 */
        if(companionLinkweaponInfo.strengthenId != selectedWeaponId) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.DONT_MATCHING_WEAPONID.getIntegerValue(), "Fail! -> Cause: Don't matching weaponID", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Don't matching weaponID", ResponseErrorCode.DONT_MATCHING_WEAPONID);
        }
        LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
                .filter(a -> a.id == selectedWeaponId)
                .findAny()
                .orElse(null);
        if(linkweaponInfoDto == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find LinkweaponInfoDto.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //선택한 웨폰의 강화단계가 이미 최종 단계인지 체크
        if(linkweaponInfoDto.upgrade >= 5) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_LEVELUP_LINKWEAPON.getIntegerValue(), "Fail! -> Cause: Cant more levelup linkweapon(MaxLevel).", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant more levelup linkweapon(MaxLevel).", ResponseErrorCode.CANT_MORE_LEVELUP_LINKWEAPON);
        }

       //링크 포인트 체크
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList();
        LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
                .filter(a->a.getID() == selectedWeaponId && a.getOwner().equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(linkweaponTalentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find linkweaponTalentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find linkweaponTalentsTable.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_LinkpointCostsForStrength = linkweaponTalentsTable.getLinkpointCostsForStrength();
        LinkpointCost linkpointCost = JsonStringHerlper.ReadValueFromJson(json_LinkpointCostsForStrength, LinkpointCost.class);
        //linkweaponInfoDto.level 1 은 진화로 인해 오픈 되었다는 뜻. 실제 강화 레벨은 해당 값 - 1이다.
        int strengthenLevel = linkweaponInfoDto.upgrade -1;
        CurrencyLogDto linkforceLogDto = new CurrencyLogDto();
        int linkforcePreviousValue = user.getLinkforcePoint();
        int spendCost = linkpointCost.cntList.get(strengthenLevel);
        if(!user.SpendLinkforcePoint(spendCost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LINKFORCEPOINT.getIntegerValue(), "Fail -> Cause: Need More LinkforcePoint", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More LinkforcePoint", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
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
        /* 패스 업적 : 링크 포스 포인트 소모 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_LINKFORCEPONT.name(), "empty", spendCost, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        linkforceLogDto.setCurrencyLogDto("링크웨폰 강화 - "+myhero.getName()+" "+linkweaponInfoDto.id+"번", "링크포인트", linkforcePreviousValue, -linkpointCost.cntList.get(strengthenLevel), user.getLinkforcePoint());
        String linkforceLog = JsonStringHerlper.WriteValueAsStringFromData(linkforceLogDto);
        loggingService.setLogging(userId, 1, linkforceLog);

        //골드 체크
        String json_GoldCostsForStrength = linkweaponTalentsTable.getGoldCostForStrength();
        GoldCost goldCost = JsonStringHerlper.ReadValueFromJson(json_GoldCostsForStrength, GoldCost.class);
        CurrencyLogDto goldLogDto = new CurrencyLogDto();
        int goldPreviousValue = user.getLinkforcePoint();
        if(!user.SpendGold(goldCost.cntList.get(strengthenLevel))) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LINKFORCEPOINT.getIntegerValue(), "Fail -> Cause: Need More LinkforcePoint", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More LinkforcePoint", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
        }
        goldLogDto.setCurrencyLogDto("링크웨폰 강화 - "+myhero.getName()+" "+linkweaponInfoDto.id+"번", "골드", goldPreviousValue, -goldCost.cntList.get(strengthenLevel), user.getGold());
        String goldLog = JsonStringHerlper.WriteValueAsStringFromData(goldLogDto);
        loggingService.setLogging(userId, 1, goldLog);

        //재료 체크
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        String json_NeedMaterialsForStrength = linkweaponTalentsTable.getNeedMaterialsForStrength();
        MaterialsCost materialsCost = JsonStringHerlper.ReadValueFromJson(json_NeedMaterialsForStrength, MaterialsCost.class);
        MaterialsCost.NeedMaterialsInfo needMaterialsInfo = materialsCost.needMaterialList.get(strengthenLevel);
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();


        for(MaterialsCost.NeedMaterial needMaterial : needMaterialsInfo.list) {

            if(needMaterial.code.equals(myCharacter.getCodeHerostable())){
                //케릭터 조각
                BelongingCharacterPieceTable characterPieceTable = belongingCharacterPieceTableList.stream()
                        .filter(a -> a.getCode().contains(needMaterial.code))
                        .findAny()
                        .orElse(null);
                if(characterPieceTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: BelongingCharacterPieceTable Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: BelongingCharacterPieceTable Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
                }

                BelongingInventory characterPiece = belongingInventoryList.stream()
                        .filter(a -> a.getItemId() == characterPieceTable.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                        .findAny()
                        .orElse(null);
                if(characterPiece == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more characterPiece", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more characterPiece", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
                }
                int needCount = needMaterial.cnt;
                int characterPieceCount = characterPiece.getCount();
                if(characterPieceCount < needCount) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHARACTERPIECE.getIntegerValue(), "Fail! -> Cause: Need more characterPiece", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more characterPiece", ResponseErrorCode.NEED_MORE_CHARACTERPIECE);
                }
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(characterPieceCount);
                characterPiece.SpendItem(needCount);
                belongingInventoryLogDto.setBelongingInventoryLogDto("링크웨폰 강화 - "+myhero.getName()+" "+linkweaponInfoDto.id+"번", characterPiece.getId(), characterPiece.getItemId(), characterPiece.getItemType(), -needCount, characterPiece.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
            }
            else if(needMaterial.code.contains("linkweapon")) {
                //브론즈 키, 실버 키, 골드 키
                SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(needMaterial.code))
                        .findAny()
                        .orElse(null);
                if(spendableItemInfoTable == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: spendableItemInfoTable Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: spendableItemInfoTable Can't Find", ResponseErrorCode.NOT_EXIST_CODE);
                }

                BelongingInventory belongingInventoryItem = belongingInventoryList.stream()
                        .filter(a -> a.getItemId() == spendableItemInfoTable.getId() && a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);
                if(belongingInventoryItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: BelongingInventory Can't Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: BelongingInventory Can't Find", ResponseErrorCode.NEED_MORE_MATERIAL);
                }
                int needCount = needMaterial.cnt;
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(belongingInventoryItem.getCount());
                if(!belongingInventoryItem.SpendItem(needCount)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_MATERIAL.getIntegerValue(), "Fail! -> Cause: Need more BelongingInventoryItem count", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Need more BelongingInventoryItem count", ResponseErrorCode.NEED_MORE_MATERIAL);
                }
                belongingInventoryLogDto.setBelongingInventoryLogDto("링크웨폰 강화 - "+myhero.getName()+" "+linkweaponInfoDto.id+"번", belongingInventoryItem.getId(), belongingInventoryItem.getItemId(), belongingInventoryItem.getItemType(), -needCount, belongingInventoryItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
            }
        }
        linkweaponInfoDto.upgrade++;

        String updated_Json_LinkweaponRevolution = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        /*정보 업데이트*/
        myLinkweaponInfo.ResetLinkweaponRevolution(updated_Json_LinkweaponRevolution);
        MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
        myCharactersBaseDto.InitFromDbData(myCharacter);
        List<BelongingInventoryDto> belongingInventoryDtoList = new ArrayList<>();
        for(BelongingInventory temp : belongingInventoryList){
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(temp);
            belongingInventoryDtoList.add(belongingInventoryDto);
        }
        map.put("myCharacter", myCharactersBaseDto);
        map.put("companionLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);
        map.put("user", user);
        map.put("belongingInventoryList", belongingInventoryDtoList);

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

    //진화
    public Map<String, Object> Revolution(Long userId, Long characterId, int selectedWeaponId, Map<String, Object> map){

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 유저의 MyLinkweapon 검색
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream()
                .filter(a -> a.ownerCode.equals(myCharacter.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not find companionweaponInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find companionweaponInfo.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        companionLinkweaponInfo.strengthenId = selectedWeaponId;

        LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
                .filter(a -> a.id == selectedWeaponId)
                .findAny()
                .orElse(null);
        if(linkweaponInfoDto == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find LinkweaponInfoDto.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //선택한 웨폰의 진화 단계가 이미 열렸는지 체크
        //if(linkweaponInfoDto.upgrade > 0)
        if(linkweaponInfoDto.open) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_MORE_OPENTALENT.getIntegerValue(), "Fail! -> Cause: Cant more open linkweapon", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant more open linkweapon", ResponseErrorCode.CANT_MORE_OPENTALENT);
        }

        //선택한 웨폰의 진화 단계가 현재 동료의 호감도 조건에 맞는지 체크
        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList();
        LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
                .filter(a->a.getID() == selectedWeaponId)
                .findAny()
                .orElse(null);
        if(linkweaponTalentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find linkweaponTalentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find linkweaponTalentsTable.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int goodfillingCondition = linkweaponTalentsTable.getLAbilityLevelCondition();
        if(myCharacter.getLinkAbilityLevel() < goodfillingCondition) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOODFEELING.getIntegerValue(), "Fail! -> Cause: Need more open GoodFeeling", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more open GoodFeeling", ResponseErrorCode.NEED_MORE_GOODFEELING);
        }

        //선택한 웨폰에 연결된 이전 단계의 강화 레벨이 Max 인지 체크
        boolean isRootLevelMax = false;
        for(int dependenciesId : linkweaponInfoDto.dependenciesList) {
            LinkweaponInfoDtosList.LinkweaponInfoDto rootWeapon = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
                    .filter(a -> a.id == dependenciesId)
                    .findAny()
                    .orElse(null);
            if(rootWeapon == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find LinkweaponInfoDto.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(rootWeapon.upgrade == 5) {
                isRootLevelMax = true;
                break;
            }
        }
        if(!isRootLevelMax) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_OPEN_TALENT.getIntegerValue(), "Fail! -> Cause: Cant more open linkweapon", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant more open linkweapon", ResponseErrorCode.NOT_YET_OPEN_TALENT);
        }
        linkweaponInfoDto.open = true;
        linkweaponInfoDto.upgrade = 1;
        companionLinkweaponInfo.selectedIds.add(linkweaponInfoDto.id);
        String updated_Json_LinkweaponRevolution = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        /*정보 업데이트*/
        myLinkweaponInfo.ResetLinkweaponRevolution(updated_Json_LinkweaponRevolution);
        map.put("companionLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);

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

        /* 업적 : 특정 스테이지 클리어 체크*/
        StringMaker.Clear();
        StringMaker.stringBuilder.append(companionLinkweaponInfo.selectedIds.size());
        StringMaker.stringBuilder.append("단계 진화");

        String stageClearParam = StringMaker.stringBuilder.toString();
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.REVOLUTION_LINK_WEAPON.name(), stageClearParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }
        return map;
    }

    //이전의 옵션 교체 요청
    public Map<String, Object> ChangeLinkweaponOption(Long userId, Long characterId, int willChangeWeaponID, Map<String, Object> map){

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myCharacters = myCharactersList.stream()
                .filter(a -> a.getId().equals(characterId))
                .findAny()
                .orElse(null);
        if(myCharacters == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<herostable> herostableList = gameDataTableService.HerosTableList();
        herostable myhero = herostableList.stream().filter(i -> i.getCode().equals(myCharacters.getCodeHerostable())).findAny().orElse(null);
        if(myhero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find herostable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find herostable", ResponseErrorCode.NOT_FIND_DATA);
        }

        //해당 유저의 MyLinkweapon 검색
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo = linkweaponInfoDtosList.companionLinkweaponInfoList.stream()
                .filter(a -> a.ownerCode.equals(myCharacters.getCodeHerostable()))
                .findAny()
                .orElse(null);
        if(companionLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not find companionweaponInfo.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find companionweaponInfo.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
                .filter(a -> a.id == willChangeWeaponID)
                .findAny()
                .orElse(null);
        if(linkweaponInfoDto == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find LinkweaponInfoDto.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //선택한 웨폰의 진화 단계가 현재 동료의 호감도 조건에 맞는지 체크
        List<LinkweaponTalentsTable> linkweaponTalentsTableList = gameDataTableService.LinkweaponTalentsTableList();
        LinkweaponTalentsTable linkweaponTalentsTable = linkweaponTalentsTableList.stream()
                .filter(a->a.getID() == willChangeWeaponID)
                .findAny()
                .orElse(null);
        if(linkweaponTalentsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find linkweaponTalentsTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not find linkweaponTalentsTable.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int goodfillingCondition = linkweaponTalentsTable.getLAbilityLevelCondition();
        if(myCharacters.getLinkAbilityLevel() < goodfillingCondition) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GOODFEELING.getIntegerValue(), "Fail! -> Cause: Need more open GoodFeeling", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more open GoodFeeling", ResponseErrorCode.NEED_MORE_GOODFEELING);
        }

        //선택한 웨폰에 연결된 이전 단계의 강화 레벨이 Max 인지 체크
        boolean isRootLevelMax = false;
        for(int dependenciesId : linkweaponInfoDto.dependenciesList) {
            LinkweaponInfoDtosList.LinkweaponInfoDto rootWeapon = companionLinkweaponInfo.linkweaponInfoDtoList.stream()
                    .filter(a -> a.id == dependenciesId)
                    .findAny()
                    .orElse(null);
            if(rootWeapon == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Not find LinkweaponInfoDto.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not find LinkweaponInfoDto.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(rootWeapon.upgrade == 5) {
                isRootLevelMax = true;
                break;
            }
        }
        if(!isRootLevelMax) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_OPEN_TALENT.getIntegerValue(), "Fail! -> Cause: Cant more open linkweapon", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Cant more open linkweapon", ResponseErrorCode.NOT_YET_OPEN_TALENT);
        }

        int revolutionStep = companionLinkweaponInfo.GetRevolutionStep(willChangeWeaponID);

        //변경 옵션이 이전에 오픈된적이 없었다면 추가 비용이 들어감.
        //if(linkweaponInfoDto.upgrade == 0) {
        if(false == linkweaponInfoDto.open) {
            linkweaponInfoDto.open = true;
            linkweaponInfoDto.upgrade = 1;
            int costDiamond = 0;
            switch(revolutionStep) {
                case 1:
                    costDiamond = 100;
                    break;
                case 2:
                    costDiamond = 200;
                    break;
                case 3:
                    costDiamond = 300;
                    break;
                case 4:
                    costDiamond = 400;
                    break;
                case 5:
                    costDiamond = 500;
                    break;
            }
            User user = userRepository.findById(userId).orElse(null);
            if(user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getDiamond();
            if(!user.SpendDiamond(costDiamond)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            currencyLogDto.setCurrencyLogDto("옵션변경 - "+myhero.getName()+" "+willChangeWeaponID+"번", "다이아", previousValue, -costDiamond, user.getDiamond());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            map.put("user", user);
        }

        companionLinkweaponInfo.selectedIds.set(revolutionStep, willChangeWeaponID);
        companionLinkweaponInfo.strengthenId = willChangeWeaponID;

        String updated_Json_LinkweaponRevolution = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        /*정보 업데이트*/
        myLinkweaponInfo.ResetLinkweaponRevolution(updated_Json_LinkweaponRevolution);
        map.put("companionLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);
        return map;
    }

    /*테스트 전용 함수. 해당 유저의 링크웨폰 정보 모두 초기화*/
    public Map<String, Object> AllClear(Long userId, Map<String, Object> map){

        //해당 유저의 MyLinkweapon 검색
        MyLinkweaponInfo myLinkweaponInfo = myLinkweaponInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myLinkweaponInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myLinkweaponInfo not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myLinkweaponInfo not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        LinkweaponInfoDtosList linkweaponInfoDtosList = JsonStringHerlper.ReadValueFromJson(myLinkweaponInfo.getJson_LinkweaponRevolution(), LinkweaponInfoDtosList.class);

        for(LinkweaponInfoDtosList.CompanionLinkweaponInfo companionLinkweaponInfo : linkweaponInfoDtosList.companionLinkweaponInfoList) {
            companionLinkweaponInfo.selectedIds.clear();
            companionLinkweaponInfo.selectedIds.add(0);
            companionLinkweaponInfo.strengthenId = 0;
            for(LinkweaponInfoDtosList.LinkweaponInfoDto linkweaponInfoDto : companionLinkweaponInfo.linkweaponInfoDtoList) {
                if(linkweaponInfoDto.id == 0) {
                    linkweaponInfoDto.upgrade = 1;
                    linkweaponInfoDto.open = true;
                }
                else {
                    linkweaponInfoDto.upgrade = 1;
                    linkweaponInfoDto.open = false;
                }
            }
        }

        String updated_Json_LinkweaponRevolution = JsonStringHerlper.WriteValueAsStringFromData(linkweaponInfoDtosList);
        /*정보 업데이트*/
        myLinkweaponInfo.ResetLinkweaponRevolution(updated_Json_LinkweaponRevolution);
        map.put("companionLinkweaponInfoList", linkweaponInfoDtosList.companionLinkweaponInfoList);
        return map;
    }
}
