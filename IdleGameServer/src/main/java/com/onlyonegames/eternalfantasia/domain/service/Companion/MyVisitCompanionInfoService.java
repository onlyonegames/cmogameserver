package com.onlyonegames.eternalfantasia.domain.service.Companion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData.TavernVisitCompanionInfoDataList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyVisitCompanionInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyVisitCompanionInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyVisitCompanionInfoService {
    private final MyVisitCompanionInfoRepository myVisitCompanionInfoRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final UserRepository userRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetMyVisitCompanionInfo(Long userId,  Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList());

        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        return map;
    }

    public static boolean ResetVisitCompanionInfo(MyVisitCompanionInfo myVisitCompanionInfo, List<MyCharacters> myCharactersList, List<herostable> herosTableList) {

        if(!myVisitCompanionInfo.IsRecycleTime(432000/*총 5일에 대한 초*/)){
            //if(!myVisitCompanionInfo.IsRecycleTime(60 * 2 + 30/*총 5일에 대한 초*/)){
            //new MyCustomException("Fail! -> Cause: Not yet visit recycletime not find.", ResponseErrorCode.NOT_YET_VISIT_RECYCLETIME);
            return false;
        }

        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = new TavernVisitCompanionInfoDataList();
        tavernVisitCompanionInfoDataList.visitList = new ArrayList<>();
        List<herostable> probabilityList = new ArrayList<>();

        for(herostable hero : herosTableList){
            //피아는 방문 동료로 나올수 없다.
            if(hero.getCode().equals("cr_003"))
                continue;
            boolean gotchaed = false;
            for(MyCharacters myCharacter : myCharactersList) {
                if(hero.getCode().equals(myCharacter.getCodeHerostable()) && myCharacter.isGotcha()){
                    gotchaed = true;
                    break;
                }
            }
            if(!gotchaed) {
                probabilityList.add(hero);
            }
        }

        int probabilitySize = probabilityList.size();
        if(probabilitySize == 0) {
            tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
            String tavernVisitCompanionInfoData = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);

            myVisitCompanionInfo.ResetVisitCompanionInfo(tavernVisitCompanionInfoData);
            return false;
        }

        int minGenerateCount = 1;
        int maxGenerateCount = probabilitySize > 6 ? 6 : probabilitySize;

        //int willGenerateCount = (int) MathHelper.Range(minGenerateCount, maxGenerateCount);
        int willGenerateCount = maxGenerateCount;//(int) MathHelper.Range(minGenerateCount, maxGenerateCount);

        for(int i = 0; i < willGenerateCount; i++) {
            int randValue = (int) MathHelper.Range(0, probabilityList.size());
            herostable hero = probabilityList.get(randValue);
            TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = new TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData(hero);
            tavernVisitCompanionInfoDataList.visitList.add(tavernVisitCompanionInfoData);
            probabilityList.remove(randValue);
        }
        tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        String tavernVisitCompanionInfoData = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);

        myVisitCompanionInfo.ResetVisitCompanionInfo(tavernVisitCompanionInfoData);
        return true;
    }

    /*
        public Legion.LegionData myCharacter;
    public MyVisitCompanionInfo myVisitCompanionInfo;
    public bool recycled;
    * */
    /*인사하기 무료*/
    public Map<String, Object> SayHello(Long userId, String heroCode, Map<String, Object> map){

        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();
        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        int relationHeroIndex = tavernVisitCompanionInfoDataList.relationHeroIndex;
        if(relationHeroIndex < 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_RELATIONED.getIntegerValue(), "Fail! -> Cause: Not yet relationed.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet relationed.", ResponseErrorCode.NOT_YET_RELATIONED);
        }

        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.get(relationHeroIndex);

        if(!tavernVisitCompanionInfoData.SayHello()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_SAYHELLO.getIntegerValue(), "Fail! -> Cause: Can't say hello.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't say hello.", ResponseErrorCode.CANT_SAYHELLO);
        }

        tavernVisitCompanionInfoDataList.completedRecruit = false;
        if(tavernVisitCompanionInfoData.linkGaugePercent >= 1000) {
            tavernVisitCompanionInfoDataList.completedRecruit = true;

            MyCharacters myCharacter = myCharactersList.stream()
                    .filter(a -> a.getCodeHerostable().equals(tavernVisitCompanionInfoData.code))
                    .findAny()
                    .orElse(null);
            if(myCharacter == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myCharacter.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not found myCharacter.", ResponseErrorCode.NOT_EXIST_CODE);
            }

            if(myCharacter.isGotcha()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GOTCHEDHEROS.getIntegerValue(), "Fail! -> Cause: Already gotched hero.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Already gotched hero.", ResponseErrorCode.ALREADY_GOTCHEDHEROS);
            }
            myCharacter.Gotcha();
            map.put("myCharacter", myCharacter);
            //tavernVisitCompanionInfoDataList.visitList.remove(tavernVisitCompanionInfoData);
            tavernVisitCompanionInfoData.Recruit();
            tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        }

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        return map;
    }
    /*호감 표시 링크포인트*/
    public Map<String, Object> HuntingTryCurrent(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();
        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        int relationHeroIndex = tavernVisitCompanionInfoDataList.relationHeroIndex;
        if(relationHeroIndex < 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_RELATIONED.getIntegerValue(), "Fail! -> Cause: Not yet relationed.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet relationed.", ResponseErrorCode.NOT_YET_RELATIONED);
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!user.SpendLinkforcePoint(10)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_LINKFORCEPOINT.getIntegerValue(), "Fail! -> Cause: Need more linkforcePoint.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more linkforcePoint.", ResponseErrorCode.NEED_MORE_LINKFORCEPOINT);
        }

        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.get(relationHeroIndex);

        if(!tavernVisitCompanionInfoData.HuntingTryCurrent()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_HUNTING.getIntegerValue(), "Fail! -> Cause: Can't hunting.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't hunting.", ResponseErrorCode.CANT_HUNTING);
        }

        tavernVisitCompanionInfoDataList.completedRecruit = false;
        if(tavernVisitCompanionInfoData.linkGaugePercent >= 1000) {
            tavernVisitCompanionInfoDataList.completedRecruit = true;

            MyCharacters myCharacter = myCharactersList.stream()
                    .filter(a -> a.getCodeHerostable().equals(tavernVisitCompanionInfoData.code))
                    .findAny()
                    .orElse(null);
            if(myCharacter == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myCharacter.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not found myCharacter.", ResponseErrorCode.NOT_EXIST_CODE);
            }

            if(myCharacter.isGotcha()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GOTCHEDHEROS.getIntegerValue(), "Fail! -> Cause: Already gotched hero.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Already gotched hero.", ResponseErrorCode.ALREADY_GOTCHEDHEROS);
            }
            myCharacter.Gotcha();
            map.put("myCharacter", myCharacter);
            //tavernVisitCompanionInfoDataList.visitList.remove(tavernVisitCompanionInfoData);
            tavernVisitCompanionInfoData.Recruit();
            tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        }

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        map.put("user", user);
        return map;
    }
    /*선물하기 선물*/
    public Map<String, Object> SendGift(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();
        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        int relationHeroIndex = tavernVisitCompanionInfoDataList.relationHeroIndex;
        if(relationHeroIndex < 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_RELATIONED.getIntegerValue(), "Fail! -> Cause: Not yet relationed.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet relationed.", ResponseErrorCode.NOT_YET_RELATIONED);
        }

        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.get(relationHeroIndex);

        if(!tavernVisitCompanionInfoData.SendGift()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_SENDGIFT.getIntegerValue(), "Fail! -> Cause: Can't send gift.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't send gift.", ResponseErrorCode.CANT_SENDGIFT);
        }

        //선물 인벤토리에서 가장 좋아하는 선물 1개 discount;
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if(myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myGiftInventory not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myGiftInventory not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String inventoryInfosString = myGiftInventory.getInventoryInfos();
        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        for(GiftTable giftTable : giftTableList) {
            if (MyGiftInventoryService.IsIncludeKindCategory(giftTable.getBestGiftTo(), heroCode)) {
                GiftItemDtosList.GiftItemDto myGiftItem = giftItemInventorys.giftItemDtoList.stream()
                        .filter(a -> a.code.equals(giftTable.getCode()))
                        .findAny()
                        .orElse(null);
                if (myGiftItem == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myGiftItem.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Not found myGiftItem.", ResponseErrorCode.NOT_EXIST_CODE);
                }

                if (!myGiftItem.SpendItem(1)) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_GIFT.getIntegerValue(), "Fail -> Cause: Need more gift", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail -> Cause: Need more gift", ResponseErrorCode.NEED_MORE_GIFT);
                }
            }
        }
        inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
        myGiftInventory.ResetInventoryInfos(inventoryInfosString);

        tavernVisitCompanionInfoDataList.completedRecruit = false;
        if(tavernVisitCompanionInfoData.linkGaugePercent >= 1000) {
            tavernVisitCompanionInfoDataList.completedRecruit = true;

            MyCharacters myCharacter = myCharactersList.stream()
                    .filter(a -> a.getCodeHerostable().equals(tavernVisitCompanionInfoData.code))
                    .findAny()
                    .orElse(null);
            if(myCharacter == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myCharacter.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not found myCharacter.", ResponseErrorCode.NOT_EXIST_CODE);
            }

            if(myCharacter.isGotcha()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GOTCHEDHEROS.getIntegerValue(), "Fail! -> Cause: Already gotched hero.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Already gotched hero.", ResponseErrorCode.ALREADY_GOTCHEDHEROS);
            }
            myCharacter.Gotcha();
            map.put("myCharacter", myCharacter);

            //tavernVisitCompanionInfoDataList.visitList.remove(tavernVisitCompanionInfoData);
            tavernVisitCompanionInfoData.Recruit();
            tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        }

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);

        return map;
    }
    /*인연 맺기*/
    public Map<String, Object> RelationStart(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();

        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.stream()
                .filter(a -> a.code.equals(heroCode))
                .findAny()
                .orElse(null);
        if(tavernVisitCompanionInfoData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found tavernVisitCompanionInfoData.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not found tavernVisitCompanionInfoData.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        tavernVisitCompanionInfoData.RelationsStart();
        tavernVisitCompanionInfoDataList.relationHeroIndex = tavernVisitCompanionInfoDataList.visitList.indexOf(tavernVisitCompanionInfoData);
        tavernVisitCompanionInfoDataList.completedRecruit = false;

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);

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
        /* 업적 : 동료와 인연 맺기 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.RELATION_START_LEGION_HERO.name(),"empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
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

    /*인연파기*/
    public Map<String, Object> BreakeCompanion(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();
        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        int relationHeroIndex = tavernVisitCompanionInfoDataList.relationHeroIndex;
        if(relationHeroIndex < 0){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_RELATIONED.getIntegerValue(), "Fail! -> Cause: Not yet relationed.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not yet relationed.", ResponseErrorCode.NOT_YET_RELATIONED);
        }

        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.get(relationHeroIndex);

        tavernVisitCompanionInfoData.BreakeCompanion();
        tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        tavernVisitCompanionInfoDataList.completedRecruit = false;

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
        return map;
    }
    /*즉시영입*/
    public Map<String, Object> InstantRecruit(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();

        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.stream()
                .filter(a -> a.code.equals(heroCode))
                .findAny()
                .orElse(null);
        if(tavernVisitCompanionInfoData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found tavernVisitCompanionInfoData.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not found tavernVisitCompanionInfoData.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        int cost =  (int)MathHelper.RoundUP(3500 * (1000 - tavernVisitCompanionInfoData.linkGaugePercent) / 1000);

        if(!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
        }

        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals(tavernVisitCompanionInfoData.code))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myCharacter.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not found myCharacter.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        if(myCharacter.isGotcha()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GOTCHEDHEROS.getIntegerValue(), "Fail! -> Cause: Already gotched hero.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already gotched hero.", ResponseErrorCode.ALREADY_GOTCHEDHEROS);
        }
        myCharacter.Gotcha();

        int relationHeroIndex = tavernVisitCompanionInfoDataList.relationHeroIndex;
        if(relationHeroIndex == -1) {
            tavernVisitCompanionInfoDataList.completedRecruit = false;
        }
        else {
            tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
            tavernVisitCompanionInfoDataList.completedRecruit = true;
        }
        //tavernVisitCompanionInfoDataList.visitList.remove(tavernVisitCompanionInfoData);
        tavernVisitCompanionInfoData.Recruit();

        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);

        map.put("myCharacter", myCharacter);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);
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
        return map;
    }
    /*해당 부분은 tavernVisitCompanionInfoData.linkGaugePercent 가 1000 이 되는순간 자동으로 영입 되도록 하는 것으로 수정되면서 사용되지 않음.*/
    /*게이지 다채워서 일반영입*/
    public Map<String, Object> Recruit(Long userId,  String heroCode, Map<String, Object> map){
        MyVisitCompanionInfo myVisitCompanionInfo = myVisitCompanionInfoRepository.findByUseridUser(userId)
                .orElse(null);
        if(myVisitCompanionInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyVisitCompanionInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyVisitCompanionInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        if(ResetVisitCompanionInfo(myVisitCompanionInfo, myCharactersList, gameDataTableService.HerosTableList())) {
            map.put("myVisitCompanionInfo", myVisitCompanionInfo);
            map.put("recycled", true);
            return map;
        }

        String visitCompanionInfo = myVisitCompanionInfo.getVisitCompanionInfo();

        TavernVisitCompanionInfoDataList tavernVisitCompanionInfoDataList = JsonStringHerlper.ReadValueFromJson(visitCompanionInfo, TavernVisitCompanionInfoDataList.class);
        TavernVisitCompanionInfoDataList.TavernVisitCompanionInfoData tavernVisitCompanionInfoData = tavernVisitCompanionInfoDataList.visitList.stream()
                .filter(a -> a.code.equals(heroCode))
                .findAny()
                .orElse(null);
        if(tavernVisitCompanionInfoData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found tavernVisitCompanionInfoData.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not found tavernVisitCompanionInfoData.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        if(tavernVisitCompanionInfoData.linkGaugePercent < 1000) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_RELATION.getIntegerValue(), "Fail! -> Cause: Need more relation.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Need more relation.", ResponseErrorCode.NEED_MORE_RELATION);
        }

        MyCharacters myCharacter = myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals(tavernVisitCompanionInfoData.code))
                .findAny()
                .orElse(null);
        if(myCharacter == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: Not found myCharacter.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Not found myCharacter.", ResponseErrorCode.NOT_EXIST_CODE);
        }

        if(myCharacter.isGotcha()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_GOTCHEDHEROS.getIntegerValue(), "Fail! -> Cause: Already gotched hero.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already gotched hero.", ResponseErrorCode.ALREADY_GOTCHEDHEROS);
        }
        myCharacter.Gotcha();

        //tavernVisitCompanionInfoDataList.visitList.remove(tavernVisitCompanionInfoData);
        tavernVisitCompanionInfoData.Recruit();
        tavernVisitCompanionInfoDataList.relationHeroIndex = -1;
        visitCompanionInfo = JsonStringHerlper.WriteValueAsStringFromData(tavernVisitCompanionInfoDataList);
        myVisitCompanionInfo.ResetVisitCompanionInfo(visitCompanionInfo);
        map.put("myCharacter", myCharacter);
        map.put("myVisitCompanionInfo", myVisitCompanionInfo);
        map.put("recycled", false);

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
        return map;
    }
}
