package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.HeroTowerExpandData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.HeroTowerStagePlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.BelongingInventoryLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.BelongingCharacterPieceTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroTowerStageTable;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerStagePlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyHeroTowerPlayService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyHeroTowerStagePlayDataRepository myHeroTowerStagePlayDataRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final GameDataTableService gameDataTableService;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;

    public Map<String, Object> HeroTowerPlayTimeSet(Long userId, Map<String, Object> map) {
        MyHeroTowerStagePlayData stagePlayData = myHeroTowerStagePlayDataRepository.findByUseridUser(userId);
        if(stagePlayData == null)
        {
            HeroTowerStagePlayDataDto newStagePlayDataDto = new HeroTowerStagePlayDataDto();
            newStagePlayDataDto.setUseridUser(userId);
            stagePlayData = myHeroTowerStagePlayDataRepository.save(newStagePlayDataDto.ToEntity());
        }

        stagePlayData.StartStagePlay();
        return map;
    }

    public Map<String, Object> StartHeroTowerPlay(Long userId, int floorNo, Map<String, Object> map) {
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
        /* 업적 : 용사의 탑 도전 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_HERO_TOWER.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

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
        /* 패스 업적 : 용사의 탑 도전 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_HERO_TOWER.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
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

    /*층수 및 실제 플레이 시간에 따른 보상을 각 인벤토리에 저장 해주고 리턴 */
    public Map<String, Object> HeroTowerClear(Long userId, float playSecond, int floorNo, Map<String, Object> map) {
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myHeroTowerExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myHeroTowerExpandSaveData.getJson_saveDataValue();
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ChapterSaveData.class);
        ChapterSaveData.ChapterPlayInfo chapterPlayInfo  = chapterSaveData.chapterData.chapterPlayInfosList.get(0);
        ChapterSaveData.StagePlayInfo stagePlayInfo = chapterPlayInfo.stagePlayInfosList.stream()
                .filter(a -> a.stageNo == floorNo && a.isOpend)
                .findAny()
                .orElse(null);
        if(stagePlayInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA.getIntegerValue(), "Fail -> Cause: Invaild floor Or not yet stage open", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Invaild floor Or not yet stage open", ResponseErrorCode.INVAILD_CHAPTER_SAVEDATA);
        }

        String json_ExpandInfo = myHeroTowerExpandSaveData.getJson_ExpandInfo();
        HeroTowerExpandData heroTowerExpandData = JsonStringHerlper.ReadValueFromJson(json_ExpandInfo, HeroTowerExpandData.class);
        int index = floorNo - 1;
        HeroTowerExpandData.HeroTowerFloorData heroTowerFloorData = heroTowerExpandData.heroTowerFloorDataList.get(index);
        List<HeroTowerStageTable> stageTableList = gameDataTableService.HeroTowerStageTableList();
        HeroTowerStageTable heroTowerStageTable = stageTableList.get(index);
        int starCount = 0;
        MyHeroTowerStagePlayData stagePlayData = myHeroTowerStagePlayDataRepository.findByUseridUser(userId);
        stagePlayData.ClearStagePlay();

        /*첫번째 타임 체크*/
        if(!GetCondition(heroTowerStageTable.getConditionStar1(), playSecond)){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.HERO_TOWER_TIME_OUT.getIntegerValue(), "Fail -> Cause: HERO_TOWER_TIME_OUT", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: HERO_TOWER_TIME_OUT", ResponseErrorCode.HERO_TOWER_TIME_OUT);
        }
        starCount++;
        /*플레이 데이터 클리어 셋팅*/

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        ItemType spendAbleItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        String[] rewardsArray = heroTowerStageTable.getStarsReward().split(",");
        if(!stagePlayInfo.isGettedStar1)
        {
            stagePlayInfo.isCleared = true;
            stagePlayInfo.isGettedStar1 = true;

            /*공통 캐릭터 조각*/

            String commonCharacterPieceRewardInfo = rewardsArray[0];
            String[] commonCharacterPieceRewardInfoArrayInfo = commonCharacterPieceRewardInfo.split(":");
            int commonCharacterPieceRewardInfoArrayInfoCount = Integer.parseInt(commonCharacterPieceRewardInfoArrayInfo[1]);
            String commonCharacterPieceCode = commonCharacterPieceRewardInfoArrayInfo[0];

            BelongingCharacterPieceTable characterPieceItemInfo = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().equals(commonCharacterPieceCode))
                    .findAny()
                    .orElse(null);
            if(characterPieceItemInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }


            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPieceItemInfo.getId())
                    .findAny()
                    .orElse(null);

            if(myCharacterPieceItem == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPieceItemInfo.getId());
                belongingInventoryDto.setCount(commonCharacterPieceRewardInfoArrayInfoCount);
                belongingInventoryDto.setItemType(spendAbleItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(0);
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), commonCharacterPieceRewardInfoArrayInfoCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
                belongingInventoryList.add(myCharacterPieceItem);
            }
            else {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(commonCharacterPieceRewardInfoArrayInfoCount, characterPieceItemInfo.getStackLimit());
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), commonCharacterPieceRewardInfoArrayInfoCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
            }
            BelongingInventoryDto myCharacterPieceItemDto = new BelongingInventoryDto();
            myCharacterPieceItemDto.InitFromDbData(myCharacterPieceItem);
            map.put("gettingFirstGift",true);
            map.put("commonCharacterPiece", myCharacterPieceItemDto);

//            User user = userRepository.findById(userId).orElse(null);
//            if(user == null) {
//                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//                throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
//            }
           // user.AddLinkforcePoint(gettingLinkPointCount);

            //user.CheckStoneOfDimensionChargingTime();
            //map.put("user", user);
        }
//        else {//첫번째 선물을 받은 경우 차원석만 갱신
//            User user = userRepository.findById(userId).orElseThrow(
//                    () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));
//
//           // user.CheckStoneOfDimensionChargingTime();
//          //  map.put("user", user);
//        }

        /*두번째 타임 체크*/
        if(GetCondition(heroTowerStageTable.getConditionStar2(), playSecond)){
            starCount++;
        }

        if(starCount == 2 && !stagePlayInfo.isGettedStar2)
        {
            stagePlayInfo.isGettedStar2 = true;

            /*2성 케릭터 조각 획득*/
            String heroPecieRewardInfo = rewardsArray[1];
            String[] heroPecieRewardInfoArray = heroPecieRewardInfo.split(":");
            int gettingHeroPeciesCount = Integer.parseInt(heroPecieRewardInfoArray[1]);

            String characterPieceCode = heroTowerFloorData.rewardCharacterPieceCodeTier2;

            BelongingCharacterPieceTable characterPieceItemInfo = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(characterPieceCode))
                    .findAny()
                    .orElse(null);
            if(characterPieceItemInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPieceItemInfo.getId())
                    .findAny()
                    .orElse(null);

            if(myCharacterPieceItem == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPieceItemInfo.getId());
                belongingInventoryDto.setCount(gettingHeroPeciesCount);
                belongingInventoryDto.setItemType(spendAbleItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(0);
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingHeroPeciesCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
                belongingInventoryList.add(myCharacterPieceItem);
            }
            else {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(gettingHeroPeciesCount, characterPieceItemInfo.getStackLimit());
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingHeroPeciesCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
            }
            BelongingInventoryDto myCharacterPieceItemDto = new BelongingInventoryDto();
            myCharacterPieceItemDto.InitFromDbData(myCharacterPieceItem);
            map.put("gettingSecondGift",true);
            map.put("myCharacterTier2PieceItem", myCharacterPieceItemDto);
        }

        /*마지막 타임 체크*/
        if(GetCondition(heroTowerStageTable.getConditionStar3(), playSecond)){
            starCount++;
        }
        if(starCount == 3 && !stagePlayInfo.isGettedStar3) {
            stagePlayInfo.isGettedStar3 = true;
            /*3성 케릭터 조각 획득*/
            String heroPecieRewardInfo = rewardsArray[2];
            String[] heroPecieRewardInfoArray = heroPecieRewardInfo.split(":");
            int gettingHeroPeciesCount = Integer.parseInt(heroPecieRewardInfoArray[1]);

            String characterPieceCode = heroTowerFloorData.rewardCharacterPieceCodeTier3;
            BelongingCharacterPieceTable characterPieceItemInfo = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().contains(characterPieceCode))
                    .findAny()
                    .orElse(null);
            if(characterPieceItemInfo == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }

            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPieceItemInfo.getId())
                    .findAny()
                    .orElse(null);

            if(myCharacterPieceItem == null) {
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPieceItemInfo.getId());
                belongingInventoryDto.setCount(gettingHeroPeciesCount);
                belongingInventoryDto.setItemType(spendAbleItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(0);
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingHeroPeciesCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
                belongingInventoryList.add(myCharacterPieceItem);
            }
            else {
                BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(gettingHeroPeciesCount, characterPieceItemInfo.getStackLimit());
                belongingInventoryLogDto.setBelongingInventoryLogDto("던전보상 - 영웅의 탑 "+floorNo+"층", myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingHeroPeciesCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
            }
            BelongingInventoryDto myCharacterPieceItemDto = new BelongingInventoryDto();
            myCharacterPieceItemDto.InitFromDbData(myCharacterPieceItem);
            map.put("gettingLastGift",true);
            map.put("myCharacterTier3PieceItem", myCharacterPieceItemDto);
        }

        json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData);
        myHeroTowerExpandSaveData.ResetSaveDataValue(json_saveDataValue);
        map.put("starCount", starCount);
        map.put("json_saveDataValue", json_saveDataValue);
        return map;
    }

    /*케릭터가 죽거나, 플레이시간이 일정 시간을 넘어가면 던전 클리어 실패*/
    public Map<String, Object> HeroTowerFail(Long userId, int floor, Map<String, Object> map) {
        /*플레이 데이터 실패 셋팅*/
        MyHeroTowerStagePlayData stagePlayData = myHeroTowerStagePlayDataRepository.findByUseridUser(userId);
        stagePlayData.DefeatStagePlay();

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

       // user.CheckStoneOfDimensionChargingTime();
        map.put("user", user);
        return map;
    }

    boolean GetCondition(String conditionString, float playSecond) {

        String[] arrayConditionString = conditionString.split(",");
        String[] arrayTimeInfoString = arrayConditionString[1].split(":");
        float minute = Float.parseFloat(arrayTimeInfoString[0]);
        float second = Float.parseFloat(arrayTimeInfoString[1]);
        second += minute * 60;

        //Duration duration = Duration.between(stagePlayData.getBattleStartTime(), stagePlayData.getBattleEndTime());
        if(playSecond <= second)
            return true;
        return false;
    }
//    boolean GetCondition(String conditionString, MyHeroTowerStagePlayData stagePlayData) {
//
//        String[] arrayConditionString = conditionString.split(",");
//        String[] arrayTimeInfoString = arrayConditionString[1].split(":");
//        float minute = Float.parseFloat(arrayTimeInfoString[0]);
//        float second = Float.parseFloat(arrayTimeInfoString[1]);
//        second += minute * 60;
//
//        Duration duration = Duration.between(stagePlayData.getBattleStartTime(), stagePlayData.getBattleEndTime());
//        if(duration.getSeconds() <= second)
//            return true;
//        return false;
//    }
}
