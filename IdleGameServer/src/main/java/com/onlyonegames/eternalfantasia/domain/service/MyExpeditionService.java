package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Event.AchieveEventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyExpeditionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Event.MyAchieveEventMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Event.MyAchieveEventMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyExpeditionService {
    private final MyExpeditionDataRepository myExpeditionDataRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyChapterSaveDataRepository myChapterSaveDataRepository;

    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;

    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyAchieveEventMissionsDataRepository myAchieveEventMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    //부스트 요청
    public Map<String, Object> boostOn(Long userId, Map<String, Object> map) {

        MyExpeditionData myExpeditionData = myExpeditionDataRepository.findByUseridUser(userId).orElse(null);
        if (myExpeditionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myExpeditionData.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myExpeditionData.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<ExpeditionProcessTable> expeditionProcessTableList = gameDataTableService.ExpeditionProcessTableList();
        ExpeditionProcessTable expeditionProcessTable = expeditionProcessTableList.get(0);

        LocalDateTime now = LocalDateTime.now();

        //하루 사용가능한 부스트수 카운트를 하루마다 클리어한다.
        Duration boostCountForDayClearDuration = Duration.between(myExpeditionData.getBoostCountForDayClearTime(), now);
        if(boostCountForDayClearDuration.getSeconds() >= 3600) {
            myExpeditionData.ResetBoostCountForDayClearTime();
        }
        //오늘 사용한 부스트 수를 체크. 맨처음 부스터는 공짜 다음부터
        int currentBoostCountForDay = myExpeditionData.getBoostCountForDay();
        if(currentBoostCountForDay > 0) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
            }

            int cost = expeditionProcessTable.getBoostCost();
            if(!user.SpendDiamond(cost)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            map.put("user", user);
        }

        //오늘 사용한 부스트 수를 체크해서 max 가 넘는지 확인.maxBoostCountPerDay 가 0으로 셋팅되어 있으면 무제한.
        int maxBoostCountPerDay = expeditionProcessTable.getBoostMaxCountPerDay();
        if(maxBoostCountPerDay > 0) {
            if(maxBoostCountPerDay <= myExpeditionData.getBoostCountForDay()){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_USE_BOOST_ANYMORE.getIntegerValue(), "Fail! -> Cause: Can't use boost anymore.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't use boost anymore.", ResponseErrorCode.CANT_USE_BOOST_ANYMORE);
            }
            myExpeditionData.SetBoostCountForDay(currentBoostCountForDay + 1);
        }

        //부스트 카운트 클리어 사이클 확인
        //이전에 부스트를 사용중이었는지 체크하고 부스트 시작한 시간과 현재시간 비교해서 완료가 되었는지 체크.
        if(myExpeditionData.isUsingBoost()) {
            Duration boostDuration = Duration.between(myExpeditionData.getBoostStartTime(), now);
            long boostDurationSecond = boostDuration.getSeconds();
            int boostTime = expeditionProcessTable.getBoostTime();
            if(boostDurationSecond < boostTime) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_USE_BOOST.getIntegerValue(), "Fail! -> Cause: Not yet use boost.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Not yet use boost.", ResponseErrorCode.NOT_YET_USE_BOOST);
            }
            myExpeditionData.SetBoostCompleteCount(myExpeditionData.getBoostCompleteCount() + 1);
        }

        //부스트 적용.
        myExpeditionData.SetBoostStartTime();
        myExpeditionData.SetIsUsingBoost(true);
        MyExpeditionDataDto myExpeditionDataDto = new MyExpeditionDataDto();
        myExpeditionDataDto.InitFromDbData(myExpeditionData);
        map.put("myExpeditionData", myExpeditionDataDto);
        return map;
    }
    //이번 탐험대 보상 받기 및 새로운 탐험대 시작
    public Map<String, Object> finishExpedition(Long userId, Map<String, Object> map) {

         MyExpeditionData myExpeditionData = myExpeditionDataRepository.findByUseridUser(userId).orElse(null);
        if (myExpeditionData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find myExpeditionData.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find myExpeditionData.", ResponseErrorCode.NOT_FIND_DATA);
        }

        //최소 탐험시간이 1분이 지나지 않았으면 보상 없음
        LocalDateTime lastExpeditionFinishTime = myExpeditionData.getLastExpeditionFinishTime();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastExpeditionFinishTime, now);
        if(duration.toMinutes() < 1) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_FINISH_EXPEDITION_TIME.getIntegerValue(), "Fail! -> Cause: Can't finish expedition time.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't finish expedition time.", ResponseErrorCode.CANT_FINISH_EXPEDITION_TIME);
        }

        List<ExpeditionProcessTable> expeditionProcessTableList = gameDataTableService.ExpeditionProcessTableList();
        ExpeditionProcessTable expeditionProcessTable = expeditionProcessTableList.get(0);

        int maxFlowTime = expeditionProcessTable.getMaxFlowtime();
        int intervalSecond = (int)duration.getSeconds();
        if(duration.getSeconds() >= maxFlowTime){
            intervalSecond = maxFlowTime;
        }

        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyChapterSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String saveDataString = myChapterSaveData.getSaveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(saveDataString, ChapterSaveData.class);

        int openStageMax = 0;
        for(ChapterSaveData.ChapterPlayInfo chapterPlayInfo : chapterSaveData.chapterData.chapterPlayInfosList) {
            for(ChapterSaveData.StagePlayInfo stagePlayInfo : chapterPlayInfo.stagePlayInfosList) {
                if(!stagePlayInfo.isCleared){
                    break;
                }
                openStageMax++;
            }
            if(!chapterPlayInfo.isOpend){
                break;
            }
        }

        //부스트 완료 횟수에 해당하는 획득량 증가 적용, 부스트 시작시간이 아직 남아있는 경우 적용된 부스트 시간에 해당하는 획득량 증가 적용
        int boostTime = expeditionProcessTable.getBoostTime();
        int boostTotalTime = myExpeditionData.getBoostCompleteCount() * boostTime;
        boostTotalTime += myExpeditionData.getRemainBoostTime();
        if(myExpeditionData.isUsingBoost()){
            Duration boostDuration = Duration.between(myExpeditionData.getBoostStartTime(), now);
            long boostDurationSecond = boostDuration.getSeconds();
            if(boostDurationSecond < boostTime) {
                //남은시간은 다음 탐험에 적용하도록 셋팅
                myExpeditionData.SetRemainBoostTime(boostTime - (int) boostDurationSecond);
                boostTotalTime += boostDurationSecond;
            }
            else{
                boostTotalTime += boostTime;
                myExpeditionData.SetIsUsingBoost(false);
                myExpeditionData.SetRemainBoostTime(0);
            }
        }
        myExpeditionData.SetBoostCompleteCount(0);

        /* 업적 : 체크 준비*/
        MyMissionsData myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if (myMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyMissionData = myMissionsData.getJson_saveDataValue();
        MissionsDataDto myMissionsDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyMissionData, MissionsDataDto.class);
        boolean changedMissionsData = false;

        /* 업적 : 탐색대 1회 수집 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GET_EXPEDITION_RESULT.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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

        /*누적 업적 : 체크 준비*/
        MyAchieveEventMissionsData myAchieveEventMissionsData = myAchieveEventMissionsDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAchieveEventMissionsData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAchieveEventMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAchieveEventMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String jsonMyAchieveEventMissionData = myAchieveEventMissionsData.getJson_saveDataValue();
        AchieveEventMissionDataDto myAchieveEventMissionDataDto = JsonStringHerlper.ReadValueFromJson(jsonMyAchieveEventMissionData, AchieveEventMissionDataDto.class);
        boolean changedAchieveEventMissionsData = false;

        /* 패스 업적 : 탐색대 1회 수집 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GET_EXPEDITION_RESULT.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        //보상 적용
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
        }
        //골드
        double goldPerSecond = MathHelper.Round2((20 + (20 * ((openStageMax - 1) * 0.07))) / 60.0);
        int gainGoldCount = (int)((intervalSecond + boostTotalTime) * goldPerSecond);
        CurrencyLogDto goldLog = new CurrencyLogDto();
        int preGold = user.getGold();
        user.AddGold(gainGoldCount);
        goldLog.setCurrencyLogDto("탐색대 보상 획득", "골드", preGold, gainGoldCount, user.getGold());
        String goldLogJson = JsonStringHerlper.WriteValueAsStringFromData(goldLog);
        loggingService.setLogging(userId, 1,  goldLogJson);
        map.put("reward_gold", gainGoldCount);

        /* 패스 업적 : 골드획득*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty",gainGoldCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;

        //링크포인트
        double linkpointPerSecond = MathHelper.Round4((2 + (2 * ((openStageMax - 1) * 0.02))) / 60);
        int gainLinkpointCount = (int)((intervalSecond + boostTotalTime) * linkpointPerSecond);
        CurrencyLogDto linkforceLog = new CurrencyLogDto();
        int preLinkforce = user.getLinkforcePoint();
        user.AddLinkforcePoint(gainLinkpointCount);
        linkforceLog.setCurrencyLogDto("탐색대 보상 획득", "링크포인트", preLinkforce, gainLinkpointCount, user.getLinkforcePoint());
        String linkforceLogJson = JsonStringHerlper.WriteValueAsStringFromData(linkforceLog);
        loggingService.setLogging(userId, 1, linkforceLogJson);
        map.put("reward_Linkpoint", gainLinkpointCount);

        //용사경험치
        double expPerSecond = MathHelper.Round4((1 + ( 1 * ((openStageMax - 1) * 0.02))) / 60);
        int gainExpCount = (int)((intervalSecond + boostTotalTime) * expPerSecond);

        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        MyCharacters myMainHero = myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals("hero"))
                .findAny()
                .orElse(null);
        if(myMainHero == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find Character", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't Find Character", ResponseErrorCode.NOT_FIND_DATA);
        }
        //경험치 적용
        int previousLevel = myMainHero.getLevel();
        //경험치 적용
        int previousExp = myMainHero.getExp();
        myMainHero.AddExp(gainExpCount, myMainHero.getMaxLevel());
        MainHeroExpLogDto mainHeroExpLogDto = new MainHeroExpLogDto();
        mainHeroExpLogDto.setMainHeroExpLogDto("탐색대 보상 획득", previousExp, gainExpCount, myMainHero.getExp());
        String expLog = JsonStringHerlper.WriteValueAsStringFromData(mainHeroExpLogDto);
        loggingService.setLogging(userId, 7, expLog);

        int maxLevel = myMainHero.getLevel();
        for(MyCharacters myCharacter : myCharactersList) {
            if(!myCharacter.getCodeHerostable().equals("hero"))
                myCharacter.SetMaxLevel(maxLevel);
        }
        int afterLevel = myMainHero.getLevel();
        //user.SetUserLevel(afterLevel);
        if(previousLevel < afterLevel) {

            //모든 동료 피로도 회복
            for(MyCharacters myCharacter : myCharactersList){
                if(!myCharacter.getCodeHerostable().equals("hero"))
                    myCharacter.SetFatigability(100);
                //     myTeamCharactersList.add(myCharacter);
            }

            for(int j = previousLevel+1; j <= afterLevel; j++) {
                /* 누적 업적 : 영웅 레벨 달성 미션 체크*/
                StringMaker.Clear();
                StringMaker.stringBuilder.append(j);
                StringMaker.stringBuilder.append(" 달성");
                String levelParameter = StringMaker.stringBuilder.toString();
                changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
                changedAchieveEventMissionsData = myAchieveEventMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.LEVEL_UP_MAIN_HERO.name(), levelParameter, gameDataTableService.AchieveEventTableList()) || changedAchieveEventMissionsData;
            }

            //SP 추가
            int addSkillPoint = afterLevel - previousLevel;
            user.AddSkillPoint(addSkillPoint);
            map.put("skill_point", addSkillPoint);

            //영웅의 탑 각 스테이지 오픈 조건 레벨 체크
            HeroTowerStageTable openableHeroTowerStageInfo = gameDataTableService.HeroTowerStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= afterLevel && a.getOpenLevel() > previousLevel)
                    .findAny()
                    .orElse(null);
            if(openableHeroTowerStageInfo != null) {
                String heroTowerChapterAndStageNoString = openableHeroTowerStageInfo.getStage();
                String[] heroTowerChapterAndStageNoStringArray =  heroTowerChapterAndStageNoString.split("-");
                int heroTowerStageNo = Integer.parseInt(heroTowerChapterAndStageNoStringArray[1]);
                MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myHeroTowerExpandSaveData == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                String json_heroTowerSaveData = myHeroTowerExpandSaveData.getJson_saveDataValue();
                ChapterSaveData heroTowerChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_heroTowerSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo heroTowerExpandChapterPlayInfo = heroTowerChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo heroTowerExpandStagePlayInfo : heroTowerExpandChapterPlayInfo.stagePlayInfosList) {
                    if(heroTowerExpandStagePlayInfo.stageNo <= heroTowerStageNo) {
                        heroTowerExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_heroTowerSaveData = JsonStringHerlper.WriteValueAsStringFromData(heroTowerChapterSaveData);
                myHeroTowerExpandSaveData.ResetSaveDataValue(json_heroTowerSaveData);
            }
            //시련의 탑 각 스테이지 오픈 조건 레벨 체크
            OrdealStageTable openableOrdealStageInfo = gameDataTableService.OrdealStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= afterLevel && a.getOpenLevel() > previousLevel)
                    .findAny()
                    .orElse(null);
            if(openableOrdealStageInfo != null) {
                String ordealChapterAndStageNoString = openableOrdealStageInfo.getStage();
                String[] ordealChapterAndStageNoStringArray =  ordealChapterAndStageNoString.split("-");
                int ordealStageNo = Integer.parseInt(ordealChapterAndStageNoStringArray[1]);
                MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myOrdealDungeonExpandSaveData == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                String json_ordealSaveData = myOrdealDungeonExpandSaveData.getJson_saveDataValue();
                ChapterSaveData ordealChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ordealSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo ordealExpandChapterPlayInfo = ordealChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo ordealExpandStagePlayInfo : ordealExpandChapterPlayInfo.stagePlayInfosList) {
                    if(ordealExpandStagePlayInfo.stageNo <= ordealStageNo) {
                        ordealExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_ordealSaveData = JsonStringHerlper.WriteValueAsStringFromData(ordealChapterSaveData);
                myOrdealDungeonExpandSaveData.ResetSaveDataValue(json_ordealSaveData);
            }
            //고대 던전 각 스테이지 오픈 조건 레벨 체크
            AncientDragonStageTable openableAncientStageInfo = gameDataTableService.AncientDragonStageTableList().stream()
                    .filter(a -> a.getOpenLevel() <= afterLevel && a.getOpenLevel() > previousLevel)
                    .findAny()
                    .orElse(null);
            if(openableAncientStageInfo != null) {
                String ancientChapterAndStageNoString = openableAncientStageInfo.getStage();
                String[] ancientChapterAndStageNoStringArray =  ancientChapterAndStageNoString.split("-");
                int ancientStageNo = Integer.parseInt(ancientChapterAndStageNoStringArray[1]);
                MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                        .orElse(null);
                if (myAncientDragonExpandSaveData == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
                }

                String json_ancientSaveData = myAncientDragonExpandSaveData.getJson_saveDataValue();
                ChapterSaveData ancientChapterSaveData = JsonStringHerlper.ReadValueFromJson(json_ancientSaveData, ChapterSaveData.class);
                ChapterSaveData.ChapterPlayInfo ancientExpandChapterPlayInfo = ancientChapterSaveData.chapterData.chapterPlayInfosList.get(0);

                for(ChapterSaveData.StagePlayInfo ancientExpandStagePlayInfo : ancientExpandChapterPlayInfo.stagePlayInfosList) {
                    if(ancientExpandStagePlayInfo.stageNo <= ancientStageNo) {
                        ancientExpandStagePlayInfo.isOpend = true;
                    }
                }
                json_ancientSaveData = JsonStringHerlper.WriteValueAsStringFromData(ancientChapterSaveData);
                myAncientDragonExpandSaveData.ResetSaveDataValue(json_ancientSaveData);
            }
        }
        map.put("reward_exp", gainExpCount);
        /*메인 영웅 레벨도 클라이언트와 싱크*/
        map.put("myMainHero", myMainHero);

        //장비 (장비 등급 확률)
        int equipmentGetSecond = expeditionProcessTable.getGainEquipmentPerSecond();
        int gainEquipmentKindCount = (intervalSecond + boostTotalTime) / equipmentGetSecond;


        List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = new ArrayList<>();
        List<HeroEquipmentInventory> heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(userId);
        for(int i = 0; i < gainEquipmentKindCount; i++){
            if(heroEquipmentInventoryList.size() >= user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }

            List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTables = gameDataTableService.HeroEquipmentClassProbabilityTableList();
            List<Double> classProbabilityList = new ArrayList<>();
            classProbabilityList.add(0D);
            classProbabilityList.add(1D);
            classProbabilityList.add(5D);
            classProbabilityList.add(10D);
            classProbabilityList.add(24D);
            classProbabilityList.add(40D);
            classProbabilityList.add(20D);
            int classIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);

            String itemClass = EquipmentCalculate.classCategoryArray[classIndex];
            int classValue = (int)EquipmentCalculate.GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTables.get(1));

            String decideGrade = "Normal";
            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
            double nomalProbability = MathHelper.Clamp(59 - (59 * (openStageMax - 1) * 0.00555), 0, 100);
            double rareProbability = MathHelper.Clamp(35 - (35 * (openStageMax - 1) * 0.00555), 0, 100);
            double heroProbability = MathHelper.Clamp(5 + (5* (openStageMax - 1) * 0.03), 0, 100);
            double legendProbability = MathHelper.Clamp(1 + (1 * (openStageMax - 1) * 0.10), 0, 100);
            double divineProbability = MathHelper.Clamp(0.1 + (0.1 * (openStageMax - 1) * 0.05), 0, 100);
            double ancientProbability = 0;
            List<Double> gradeProbabilityList = new ArrayList<>();
            gradeProbabilityList.add(nomalProbability);
            gradeProbabilityList.add(rareProbability);
            gradeProbabilityList.add(heroProbability);
            gradeProbabilityList.add(legendProbability);
            gradeProbabilityList.add(divineProbability);
            gradeProbabilityList.add(ancientProbability);
            int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);

            switch (selectedIndex) {
                case 0:
                    decideGrade = "Normal";
                    break;
                case 1:
                    decideGrade = "Rare";
                    break;
                case 2:
                    decideGrade = "Hero";
                    break;
                case 3:
                    decideGrade = "Legend";
                    break;
                case 4:
                    decideGrade = "Divine";
                    break;
                case 5:
                    decideGrade = "Ancient";
                    break;
            }

            List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, decideGrade);

            int randValue = (int)MathHelper.Range(0, probabilityList.size());
            HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
            List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();

            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
            HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(userId, selectEquipment, itemClass, classValue, optionsInfoTableList);
            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            equipmentLogDto.setEquipmentLogDto("탐색대 보상 획득 - ["+generatedItem.getId()+"]", generatedItem.getId(), "추가",heroEquipmentInventoryDto);
            String equipmentLog = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
            loggingService.setLogging(userId, 2, equipmentLog);

            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), itemClass, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectEquipment.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        }
        map.put("changedHeroEquipmentInventoryList", changedHeroEquipmentInventoryList);
        //선물
        int giftGetCount = 1;
        int giftGetSecond = expeditionProcessTable.getGainGiftPerSecond();
        int giftKindCount = (intervalSecond + boostTotalTime) / giftGetSecond;
        MyGiftInventory myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                .orElse(null);
        if (myGiftInventory == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
        List<GiftItemDtosList.GiftItemDto> gettedGiftItemList = new ArrayList<>();
        String inventoryInfosString = myGiftInventory.getInventoryInfos();
        GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);
        List<GiftItemDtosList.GiftItemDto> previousGift = new ArrayList<>();
        for(GiftItemDtosList.GiftItemDto temp: giftItemInventorys.giftItemDtoList){
            GiftItemDtosList.GiftItemDto giftItemDto = new GiftItemDtosList.GiftItemDto(temp.code,temp.count);
            previousGift.add(giftItemDto);
        }
        for(int i = 0; i < giftKindCount; i++) {
//            int randomIndex = 0;
//            GiftItemDtosList.GiftItemDto giftItemDto = new GiftItemDtosList.GiftItemDto();
//            do {
//                randomIndex = (int) MathHelper.Range(0, giftItemInventorys.giftItemDtoList.size());
//                giftItemDto = giftItemInventorys.giftItemDtoList.get(randomIndex);
//            } while (giftItemDto.code.equals("gift_025"));
            List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
            copyGiftTableList.remove(25);
            int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
            GiftTable giftTable = copyGiftTableList.get(randIndex);

            GiftItemDtosList.GiftItemDto giftItemDto = giftItemInventorys.giftItemDtoList.stream()
                    .filter(a -> a.code.equals(giftTable.getCode()))
                    .findAny()
                    .orElse(null);
            if(giftItemDto == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
            }

            giftItemDto.AddItem(giftGetCount, giftTable.getStackLimit());
            GiftItemDtosList.GiftItemDto resultItem = gettedGiftItemList.stream()
                    .filter( a -> a.code.equals(giftItemDto.code))
                    .findAny()
                    .orElse(null);
            if(resultItem != null) {
                resultItem.AddItem(giftGetCount, giftTable.getStackLimit());
            }
            else{
                GiftItemDtosList.GiftItemDto generatedGiftItem = new GiftItemDtosList.GiftItemDto();
                generatedGiftItem.code = giftItemDto.code;
                generatedGiftItem.count = 1;
                gettedGiftItemList.add(generatedGiftItem);
            }
        }
        for(int i = 0; i<giftItemInventorys.giftItemDtoList.size();i++){
            for(GiftItemDtosList.GiftItemDto temp: gettedGiftItemList){
                if(giftItemInventorys.giftItemDtoList.get(i).code.equals(temp.code)){
                    GiftLogDto giftLogDto = new GiftLogDto();
                    int previousValue = previousGift.size()>i?previousGift.get(i).count:0;
                    giftLogDto.setPreviousValue(previousValue);
                    giftLogDto.setGiftLogDto("탐색대 보상 획득", temp.code, temp.count, giftItemInventorys.giftItemDtoList.get(i).count);
                    String giftLog = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
                    loggingService.setLogging(userId, 4, giftLog);
                }
            }
        }

        inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
        /*정보 업데이트*/
        myGiftInventory.ResetInventoryInfos(inventoryInfosString);
        map.put("gettedGiftItemList", gettedGiftItemList);
        //재료
        int materialCount = (int) MathHelper.RoundUP(8 + (5 * ((openStageMax - 1) * 0.01)));
        int materialGetSecond = expeditionProcessTable.getGainMaterialPerSecond();
        int gainmaterialKindCount = (intervalSecond + boostTotalTime) / materialGetSecond;
        List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<BelongingInventoryDto> changedList = new ArrayList<>();
        List<BelongingInventoryDto> previousInventoryList = new ArrayList<>();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        ItemType materialItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                .findAny()
                .orElse(null);
        if(materialItemType == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        for(BelongingInventory temp:belongingInventoryList) {
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.InitFromDbData(temp);
            previousInventoryList.add(belongingInventoryDto);
        }

        for(int i = 0; i < gainmaterialKindCount; i++) {

            int randomIndex = (int) MathHelper.Range(0, equipmentMaterialInfoTableList.size());
            EquipmentMaterialInfoTable selectedMaterial = equipmentMaterialInfoTableList.get(randomIndex);
            String rewardCode = selectedMaterial.getCode();
            EquipmentMaterialInfoTable equipmentMaterial = equipmentMaterialInfoTableList.stream()
                    .filter(a -> a.getCode().equals(rewardCode))
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
                inventoryItem.AddItem(materialCount, equipmentMaterial.getStackLimit());

                BelongingInventoryDto changeItem = changedList.stream()
                        .filter( a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                        .findAny()
                        .orElse(null);
                if(changeItem == null) {
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.InitFromDbData(inventoryItem);
                    belongingInventoryDto.setCount(materialCount);
                    changedList.add(belongingInventoryDto);
                }
                else
                    changeItem.AddCount(materialCount);
            }
            else {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(equipmentMaterial.getId());
                belongingInventoryDto.setCount(materialCount);
                belongingInventoryDto.setItemType(materialItemType);
                BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                belongingInventoryList.add(willAddBelongingInventoryItem);

                BelongingInventoryDto changeItem = changedList.stream()
                        .filter( a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Material && a.getItemId() == equipmentMaterial.getId())
                        .findAny()
                        .orElse(null);
                if(changeItem == null) {
                    belongingInventoryDto.InitFromDbData(willAddBelongingInventoryItem);
                    changedList.add(belongingInventoryDto);
                }
                else
                    changeItem.AddCount(materialCount);
            }
        }

        //강화석
        int enchantItemGettingCount = 1;
        int enchantItemPerSecond = expeditionProcessTable.getGainEnchantItemPerSecond();
        int gainEnchantItemKindCount = (intervalSecond + boostTotalTime) / enchantItemPerSecond;
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<SpendableItemInfoTable> enchantItemList = new ArrayList<>();
        for(SpendableItemInfoTable spendableItemInfoTable : spendableItemInfoTableList) {
            if(spendableItemInfoTable.getCode().contains("enchant")){
                enchantItemList.add(spendableItemInfoTable);
            }
        }
        ItemType spendableItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                .findAny()
                .orElse(null);
        if(spendableItemType == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ItemType Can't find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ItemType Can't find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        for(int i = 0; i < gainEnchantItemKindCount; i++){

            double nomalProbability = 44 - (44 * (openStageMax - 1) * 0.00355);
            double rareProbability = 35 - (35 * (openStageMax - 1) * 0.00355);
            double heroProbability = 15 + (15* (openStageMax - 1) * 0.005);
            double legendProbability = 5 + (5 * (openStageMax - 1) * 0.04);
            double divineProbability = 1 + (1 * (openStageMax - 1) * 0.005);
            double ancientProbability = 0.05 + (0.05 * (openStageMax - 1) * 0.05);
            List<Double> gradeProbabilityList = new ArrayList<>();
            gradeProbabilityList.add(nomalProbability);
            gradeProbabilityList.add(rareProbability);
            gradeProbabilityList.add(heroProbability);
            gradeProbabilityList.add(legendProbability);
            gradeProbabilityList.add(divineProbability);
            gradeProbabilityList.add(ancientProbability);
            int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
            String decideGrade = "";
            switch (selectedIndex) {
                case 0:
                    decideGrade = "Normal";
                    break;
                case 1:
                    decideGrade = "Rare";
                    break;
                case 2:
                    decideGrade = "Hero";
                    break;
                case 3:
                    decideGrade = "Legend";
                    break;
                case 4:
                    decideGrade = "Divine";
                    break;
                case 5:
                    decideGrade = "Ancient";
                    break;
            }
            String finalDecideGrade = decideGrade;
            SpendableItemInfoTable spendAbleItem = enchantItemList.stream()
                    .filter(a -> a.getSpendableType().equals("MATERIAL_FOR_STRENGTHEN") && a.getGrade().equals(finalDecideGrade))
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
                inventoryItem.AddItem(enchantItemGettingCount, spendAbleItem.getStackLimit());

                BelongingInventoryDto changeItem = changedList.stream()
                        .filter( a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == spendAbleItem.getId())
                        .findAny()
                        .orElse(null);
                if(changeItem == null) {

                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.InitFromDbData(inventoryItem);
                    belongingInventoryDto.setCount(enchantItemGettingCount);
                    changedList.add(belongingInventoryDto);
                }
                else
                    changeItem.AddCount(enchantItemGettingCount);
            }
            else {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(spendAbleItem.getId());
                belongingInventoryDto.setCount(enchantItemGettingCount);
                belongingInventoryDto.setItemType(spendableItemType);
                BelongingInventory willAddBelongingInventoryItem = belongingInventoryDto.ToEntity();
                willAddBelongingInventoryItem = belongingInventoryRepository.save(willAddBelongingInventoryItem);
                belongingInventoryList.add(willAddBelongingInventoryItem);

                BelongingInventoryDto changeItem = changedList.stream()
                        .filter( a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_Spendables && a.getItemId() == spendAbleItem.getId())
                        .findAny()
                        .orElse(null);
                if(changeItem == null) {
                    belongingInventoryDto.InitFromDbData(willAddBelongingInventoryItem);
                    changedList.add(belongingInventoryDto);
                }
                else
                    changeItem.AddCount(enchantItemGettingCount);
            }
        }
        if(changedList.size() > 0)
            map.put("rewardBelongingList", changedList);
        for(BelongingInventoryDto temp:changedList) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            BelongingInventory belongingInventory = belongingInventoryList.stream().filter(i->i.getItemType().equals(temp.getItemType()) && i.getItemId() == temp.getItemId())
                    .findAny().orElse(null);
            if(belongingInventory == null){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: BelongingInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: BelongingInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            BelongingInventoryDto previousItem = previousInventoryList.stream().filter(i->i.getItemType().equals(temp.getItemType())&&i.getItemId() == temp.getItemId())
                    .findAny().orElse(null);
            if (previousItem == null) {
                belongingInventoryLogDto.setPreviousValue(0);
            } else {
                belongingInventoryLogDto.setPreviousValue(previousItem.getCount());
            }
            belongingInventoryLogDto.setBelongingInventoryLogDto("탐색대 보상 획득", belongingInventory.getId(), belongingInventory.getItemId(),belongingInventory.getItemType(),temp.getCount(),belongingInventory.getCount());
            String belongingLog = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
            loggingService.setLogging(userId, 3, belongingLog);
        }
        //탐색대 완료시간 갱신.
        myExpeditionData.ResetLastExpeditionFinishTime();
        MyExpeditionDataDto myExpeditionDataDto = new MyExpeditionDataDto();
        myExpeditionDataDto.InitFromDbData(myExpeditionData);
        map.put("myExpeditionData", myExpeditionDataDto);
        map.put("intervalSecond", intervalSecond);

        /* 업적 : 미션 데이터 변경점 적용*/
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
        /* 누적 업적 : 미션 데이터 변경점 적용*/
        if(changedAchieveEventMissionsData) {
            jsonMyAchieveEventMissionData = JsonStringHerlper.WriteValueAsStringFromData(myAchieveEventMissionDataDto);
            myAchieveEventMissionsData.ResetSaveDataValue(jsonMyAchieveEventMissionData);
            map.put("exchange_myAchieveEventMisstionsData", true);
            map.put("myAchieveEventMissionsDataDto", myAchieveEventMissionDataDto);
        }
        return map;
    }
}
