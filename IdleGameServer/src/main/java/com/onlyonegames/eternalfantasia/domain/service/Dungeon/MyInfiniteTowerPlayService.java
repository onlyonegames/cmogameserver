package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.MailDto.MailGettingItemsResponseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.InfiniteTowerRecords;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteTowerRedisScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonStagePlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.Mail;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.Companion.MyGiftInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.InfinityTowerRecordsRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.InfiniteRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.InfiniteTowerRedisScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyInfiniteTowerPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyInfiniteTowerSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.HeroEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.InfinityLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.etc.ApplySomeReward;
import com.onlyonegames.eternalfantasia.etc.EquipmentCalculate;
import com.onlyonegames.eternalfantasia.etc.EquipmentItemCategory;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyInfiniteTowerPlayService {
    private final MyInfiniteTowerPlayDataRepository myInfiniteTowerPlayDataRepository;
    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;
    private final GameDataTableService gameDataTableService;
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final ItemTypeRepository itemTypeRepository;
    private final UserRepository userRepository;
    private final HeroEquipmentInventoryRepository heroEquipmentInventoryRepository;
    private final MyGiftInventoryRepository myGiftInventoryRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final InfiniteRankingRepository infiniteRankingRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final InfiniteTowerRedisScoreRepository infiniteTowerRedisScoreRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LoggingService loggingService;
    private final InfinityTowerRecordsRepository infinityTowerRecordsRepository;
    private final MyProfileDataRepository myProfileDataRepository;
    public Map<String, Object> InfiniteTowerPlayTimeSet(Long userId, Map<String, Object> map) {
        MyInfiniteTowerPlayData stagePlayData = myInfiniteTowerPlayDataRepository.findByUseridUser(userId);
        if(stagePlayData == null)
        {
            MyInfiniteTowerPlayDataDto newStagePlayDataDto = new MyInfiniteTowerPlayDataDto();
            newStagePlayDataDto.setUseridUser(userId);
            stagePlayData = myInfiniteTowerPlayDataRepository.save(newStagePlayDataDto.ToEntity());
        }

        stagePlayData.StartStagePlay();
        return map;
    }

    public Map<String, Object> StartInfiniteTowerPlay(Long userId, int floorNo, Map<String, Object> map) {

        return map;
    }

    public Map<String, Object> InfiniteTowerClear(Long userId, int floorNo, Map<String, Object> map) {

        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: myInfiniteTowerSaveDataRepository not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: myInfiniteTowerSaveDataRepository not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(floorNo != myInfiniteTowerSaveData.getArrivedTopFloor()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_YET_PLAY_INFINITETOWER.getIntegerValue(), "Fail! -> Cause: not yet play InfiniteTower.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not yet play InfiniteTower.", ResponseErrorCode.NOT_YET_PLAY_INFINITETOWER);
        }
        List<InfiniteTowerStageTable> infiniteTowerStageTableList = gameDataTableService.InfiniteTowerStageTableList();
        InfiniteTowerStageTable lastInfiniteTowerStag = infiniteTowerStageTableList.get(infiniteTowerStageTableList.size() - 1);
        int lastFloorNo = lastInfiniteTowerStag.getInfinitetowerstageinfotable_id();
        if(floorNo <= lastFloorNo )
            myInfiniteTowerSaveData.SetArrivedTopFloor(floorNo + 1);

        MyInfiniteTowerPlayData stagePlayData = myInfiniteTowerPlayDataRepository.findByUseridUser(userId);
        stagePlayData.ClearStagePlay();

        InfiniteTowerStageTable infiniteTowerStageTable = infiniteTowerStageTableList.stream()
                .filter(a -> a.getInfinitetowerstageinfotable_id() == floorNo)
                .findAny()
                .orElse(null);
        if(infiniteTowerStageTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find infiniteTowerStageTable.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Can't Find infiniteTowerStageTable.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardsArray = infiniteTowerStageTable.getFirstReward().split(",");


        MyMissionsData myMissionsData = null;
        /*업적 : 체크 준비*/
        if(myMissionsData == null) {
            myMissionsData = myMissionsDataRepository.findByUseridUser(userId)
                    .orElse(null);
            if(myMissionsData == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyMissionsData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: MyMissionsData not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
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

        MailGettingItemsResponseDto mailGettingItemsResponseDto = new MailGettingItemsResponseDto();
        for(int i = 0; i < rewardsArray.length; i++) {
            String rewardInfos = rewardsArray[i];
            String[] rewardInfoArray = rewardInfos.split(":");
            String rewardInfo = rewardInfoArray[0];
            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
            changedMissionsData = receiveItem(userId, rewardInfo, gettingCount, mailGettingItemsResponseDto, myMissionsDataDto, "던전보상 - 무한의 탑 "+floorNo+"층") || changedMissionsData;
            if(rewardInfo.equals("gold") || rewardInfo.equals("Gold")) {
                /* 패스 업적 : 골드획득*/
                changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.ERNING_GOLD.name(),"empty", gettingCount, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
            }
            map.put("rewards", mailGettingItemsResponseDto);
        }

        //팀동료 케릭터들 피로도 깍기
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String[] myTeams = myTeamInfo.getInfiniteTowerTeam().split(",");
        List<Long> teamIdList = new ArrayList<>();
        for(int i = 0; i < myTeams.length; i++) {
            Long characterId = Long.parseLong(myTeams[i]);
            if(!characterId.equals(0L)) {
                teamIdList.add(characterId);
            }
        }
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        // List<MyCharacters> myTeamCharactersList = new ArrayList<>();
        //메인영웅(code:hero)은 제외
        List<Long> characterId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
        int spendFatigability = 10;
        for(MyCharacters myCharacter : myCharactersList){
            for(Long myTeamCharactersId : teamIdList){
                if(myCharacter.getId().equals(myTeamCharactersId)){
                    if(!myCharacter.getCodeHerostable().equals("hero")) {
                        characterId.add(myCharacter.getId());
                        previousFatigability.add(myCharacter.getFatigability());
                        myCharacter.SpendFatigability(spendFatigability);
                        presentFatigability.add(myCharacter.getFatigability());
                        /* 패스 업적 : 피로도 소모 체크*/
                        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_FATIGABILITY.name(),"empty", spendFatigability, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                    }
                    //     myTeamCharactersList.add(myCharacter);
                }
            }
        }
        if(!characterId.isEmpty()){
            CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("무한의 탑 "+floorNo+"층 진행", characterId, previousFatigability, presentFatigability);
            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
            loggingService.setLogging(userId, 5, fatigabilityLog);
        }
        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
        for(MyCharacters temp : myCharactersList){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(temp);
            myCharactersBaseDtoList.add(myCharactersBaseDto);
        }
        map.put("myCharactersList",myCharactersBaseDtoList);

        /* 패스 업적 : 미션 데이터 변경점 적용*/
        if(changedEternalPassMissionsData) {
            jsonMyEternalPassMissionData = JsonStringHerlper.WriteValueAsStringFromData(myEternalPassMissionDataDto);
            myEternalPassMissionsData.ResetSaveDataValue(jsonMyEternalPassMissionData);
            map.put("exchange_myEternalPassMissionsData", true);
            map.put("myEternalPassMissionsDataDto", myEternalPassMissionDataDto);
            map.put("myEternalPassLastDailyMissionClearTime", myEternalPassMissionsData.getLastDailyMissionClearTime());
            map.put("myEternalPassLastWeeklyMissionClearTime", myEternalPassMissionsData.getLastWeeklyMissionClearTime());
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
        MyInfiniteTowerSaveDataDto myInfiniteTowerSaveDataDto = new MyInfiniteTowerSaveDataDto();
        myInfiniteTowerSaveDataDto.setId(myInfiniteTowerSaveData.getId());
        myInfiniteTowerSaveDataDto.setUseridUser(myInfiniteTowerSaveData.getUseridUser());
        myInfiniteTowerSaveDataDto.setArrivedTopFloor(myInfiniteTowerSaveData.getArrivedTopFloor());
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(myInfiniteTowerSaveData.getReceivedRewardInfoJson(), MyInfiniteTowerRewardReceivedInfosDto.class);
        myInfiniteTowerSaveDataDto.setMyInfiniteTowerRewardReceivedInfosDto(myInfiniteTowerRewardReceivedInfosDto);
        map.put("myInfiniteTowerSaveData",myInfiniteTowerSaveDataDto);

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        InfiniteRanking infiniteRanking = infiniteRankingRepository.findByUseridUser(userId).orElse(null);

        setScore(user, infiniteRanking, floorNo);
        return map;
    }

    public Map<String, Object> InfiniteTowerFail(Long userId, int floor, Map<String, Object> map) {
        /*플레이 데이터 실패 셋팅*/
        MyInfiniteTowerPlayData stagePlayData = myInfiniteTowerPlayDataRepository.findByUseridUser(userId);
        stagePlayData.DefeatStagePlay();

//        //팀동료 케릭터들 피로도 깍기
//        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
//        String[] myTeams = myTeamInfo.getOrdealDungeonPlayTeam().split(",");
//        List<Long> teamIdList = new ArrayList<>();
//        for(int i = 0; i < myTeams.length; i++) {
//            Long characterId = Long.parseLong(myTeams[i]);
//            if(!characterId.equals(0L)) {
//                teamIdList.add(characterId);
//            }
//        }
//
//        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
//
//        // List<MyCharacters> myTeamCharactersList = new ArrayList<>();
//        //메인영웅(code:hero)은 제외
//        List<Long> characterId = new ArrayList<>();
//        List<Integer> previousFatigability = new ArrayList<>();
//        List<Integer> presentFatigability = new ArrayList<>();
//        for(MyCharacters myCharacter : myCharactersList){
//            for(Long myTeamCharactersId : teamIdList){
//                if(myCharacter.getId().equals(myTeamCharactersId)){
//                    if(!myCharacter.getCodeHerostable().equals("hero")) {
//                        characterId.add(myCharacter.getId());
//                        previousFatigability.add(myCharacter.getFatigability());
//                        myCharacter.SpendFatigability(10);
//                        presentFatigability.add(myCharacter.getFatigability());
//                    }
//                    //     myTeamCharactersList.add(myCharacter);
//                }
//            }
//        }
//        if(!characterId.isEmpty()){
//            CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
//            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("무한의 탑 "+floor+"층 진행", characterId, previousFatigability, presentFatigability);
//            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
//            loggingService.setLogging(userId, 5, fatigabilityLog);
//        }
//        List<MyCharactersBaseDto> myCharactersBaseDtoList = new ArrayList<>();
//        for(MyCharacters temp : myCharactersList){
//            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
//            myCharactersBaseDto.InitFromDbData(temp);
//            myCharactersBaseDtoList.add(myCharactersBaseDto);
//        }
//        map.put("myCharactersList",myCharactersBaseDtoList);
        return map;
    }

    private boolean receiveItem(Long userId, String gettingItemCode, int gettingCount,  MailGettingItemsResponseDto mailGettingItemsResponseDto, MissionsDataDto myMissionsDataDto, String workingPosition) {

        List<BelongingCharacterPieceTable> orignalBelongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<BelongingCharacterPieceTable> copyBelongingCharacterPieceTableList = new ArrayList<>();
        for(BelongingCharacterPieceTable characterPieceTable : orignalBelongingCharacterPieceTableList) {
            if(characterPieceTable.getCode().equals("characterPieceAll"))
                continue;
            copyBelongingCharacterPieceTableList.add(characterPieceTable);
        }
        List<BelongingInventory> belongingInventoryList = null;
        List<ItemType> itemTypeList = null;
        ItemType spendAbleItemType = null;
        ItemType materialItemType = null;
        User user = null;
        MyGiftInventory myGiftInventory = null;
        List<MyCharacters> myCharactersList = null;
        List<HeroEquipmentInventory> heroEquipmentInventoryList = null;

        boolean changedMissionsData = false;

        //피로도 50 회복 물약, 즉시 제작권, 차원석, 강화석, 재련석, 링크웨폰키, 코스튬 무료 티켓
        if(gettingItemCode.equals("recovery_fatigability") || gettingItemCode.equals("ticket_direct_production_equipment")
                || gettingItemCode.equals("dimensionStone") || gettingItemCode.contains("enchant") || gettingItemCode.contains("resmelt")
                || gettingItemCode.equals("linkweapon_bronzeKey") || gettingItemCode.equals("linkweapon_silverKey")
                || gettingItemCode.equals("linkweapon_goldKey") || gettingItemCode.equals("costume_ticket")) {
            if(belongingInventoryList == null)
                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
            if(itemTypeList == null)
                itemTypeList = itemTypeRepository.findAll();
            if(spendAbleItemType == null)
                spendAbleItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                        .findAny()
                        .orElse(null);
            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
            BelongingInventoryDto belongingInventoryDto = ApplySomeReward.ApplySpendableItem(userId, gettingItemCode, gettingCount, belongingInventoryRepository, belongingInventoryList, spendableItemInfoTableList, spendAbleItemType, workingPosition, loggingService, errorLoggingService);
            Long findId = belongingInventoryDto.getId();
            BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getId().equals(findId))
                    .findAny()
                    .orElse(null);
            if(findBelongingInventoryDto == null) {
                receivedSpendableItemList.add(belongingInventoryDto);
            }
            else {
                findBelongingInventoryDto.AddCount(gettingCount);
            }
        }
        //골드
        else if(gettingItemCode.equals("gold") || gettingItemCode.equals("Gold")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getGold();
            user.AddGold(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "골드", previousValue, gettingCount, user.getGold());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingGold(gettingCount);
        }
        //다이아
        else if(gettingItemCode.equals("diamond") || gettingItemCode.equals("Diamond")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getDiamond();
            user.AddDiamond(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "다이아", previousValue, gettingCount, user.getDiamond());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingDiamond(gettingCount);
        }
        //링크 포인트
        else if(gettingItemCode.equals("linkPoint") || gettingItemCode.equals("Linkpoint")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getLinkforcePoint();
            user.AddLinkforcePoint(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "링크포인트", previousValue, gettingCount, user.getLinkforcePoint());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingLinkPoint(gettingCount);
        }
        //아레나 코인
        else if(gettingItemCode.equals("arenaCoin")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getArenaCoin();
            user.AddArenaCoin(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingArenaCoin(gettingCount);
        }
        //아레나 티켓
        else if(gettingItemCode.equals("arenaTicket")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getArenaTicket();
            user.AddArenaTicket(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "아레나 티켓", previousValue, gettingCount, user.getArenaTicket());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingArenaTicket(gettingCount);
        }
        else if(gettingItemCode.equals("lowDragonScale")) {
            if(user == null) {
                user = userRepository.findById(userId)
                        .orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getLowDragonScale();
            user.AddLowDragonScale(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "용의 비늘(전설)", previousValue, gettingCount, user.getLowDragonScale());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingLowDragonScale(gettingCount);
        }
        else if(gettingItemCode.equals("middleDragonScale")) {
            if(user == null) {
                user = userRepository.findById(userId)
                        .orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getMiddleDragonScale();
            user.AddMiddleDragonScale(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "용의 비늘(신성)", previousValue, gettingCount, user.getMiddleDragonScale());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingMiddleDragonScale(gettingCount);
        }
        else if(gettingItemCode.equals("highDragonScale")) {
            if(user == null) {
                user = userRepository.findById(userId)
                        .orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            CurrencyLogDto currencyLogDto = new CurrencyLogDto();
            int previousValue = user.getHighDragonScale();
            user.AddHighDragonScale(gettingCount);
            currencyLogDto.setCurrencyLogDto(workingPosition, "용의 비늘(고대)", previousValue, gettingCount, user.getHighDragonScale());
            String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
            loggingService.setLogging(userId, 1, log);
            mailGettingItemsResponseDto.AddGettingHighDragonScale(gettingCount);
        }
        /*3종, 5종, 8종 재료 상자*/
        else if(gettingItemCode.equals("reward_material_low") || gettingItemCode.equals("reward_material_middle") || gettingItemCode.equals("reward_material_high")) {
            int kindCount = 0;
            if (gettingItemCode.contains("low")) {
                //3종
                kindCount = 3;
            } else if (gettingItemCode.contains("middle")) {
                //5종
                kindCount = 5;
            }
            else if (gettingItemCode.contains("high")) {
                //8종
                kindCount = 8;
            }
            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
            if(belongingInventoryList == null)
                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
            if(itemTypeList == null)
                itemTypeList = itemTypeRepository.findAll();
            if(materialItemType == null) {
                materialItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                        .findAny()
                        .orElse(null);
            }
            List<EquipmentMaterialInfoTable> copyEquipmentMaterialInfoTableList = new ArrayList<>();
            copyEquipmentMaterialInfoTableList.addAll(equipmentMaterialInfoTableList);
            int addIndex = 0;
            while (addIndex < kindCount) {
                int selectedRandomIndex = (int) MathHelper.Range(0, copyEquipmentMaterialInfoTableList.size());
                EquipmentMaterialInfoTable equipmentMaterialInfoTable = copyEquipmentMaterialInfoTableList.get(selectedRandomIndex);
                BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(equipmentMaterialInfoTable.getCode(), gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, workingPosition, userId, loggingService, errorLoggingService);

                Long findId = belongingInventoryDto.getId();
                BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getId().equals(findId))
                        .findAny()
                        .orElse(null);
                if(findBelongingInventoryDto == null) {
                    receivedSpendableItemList.add(belongingInventoryDto);
                }
                else {
                    findBelongingInventoryDto.AddCount(gettingCount);
                }
                copyEquipmentMaterialInfoTableList.remove(selectedRandomIndex);
                addIndex++;
            }
        }
        /*특정 재료*/
        else if(gettingItemCode.contains("material")){
            List<BelongingInventoryDto> receivedSpendableItemList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
            if(belongingInventoryList == null)
                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            List<EquipmentMaterialInfoTable> equipmentMaterialInfoTableList = gameDataTableService.EquipmentMaterialInfoTableList();
            if(itemTypeList == null)
                itemTypeList = itemTypeRepository.findAll();
            if(materialItemType == null) {
                materialItemType = itemTypeList.stream()
                        .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                        .findAny()
                        .orElse(null);
            }
            BelongingInventoryDto belongingInventoryDto = ApplySomeReward.AddEquipmentMaterialItem(gettingItemCode, gettingCount, belongingInventoryRepository, itemTypeList, belongingInventoryList, equipmentMaterialInfoTableList, materialItemType, workingPosition, userId, loggingService, errorLoggingService);

            Long findId = belongingInventoryDto.getId();
            BelongingInventoryDto findBelongingInventoryDto = receivedSpendableItemList.stream().filter(a -> a.getId().equals(findId))
                    .findAny()
                    .orElse(null);
            if(findBelongingInventoryDto == null) {
                receivedSpendableItemList.add(belongingInventoryDto);
            }
            else {
                findBelongingInventoryDto.AddCount(gettingCount);
            }
        }
        /*모든 선물중 하나*/
        else if(gettingItemCode.equals("giftAll")) {
            List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
            List<GiftTable> copyGiftTableList = new ArrayList<>(giftTableList);
            copyGiftTableList.remove(25);
            int randIndex = (int) MathHelper.Range(0, copyGiftTableList.size());
            GiftTable giftTable = copyGiftTableList.get(randIndex);
            if(myGiftInventory == null) {
                myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId)
                        .orElse(null);
                if(myGiftInventory == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
            String inventoryInfosString = myGiftInventory.getInventoryInfos();
            GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

            GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                    .filter(a -> a.code.equals(giftTable.getCode()))
                    .findAny()
                    .orElse(null);
            if(inventoryItemDto == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
            }

            GiftLogDto giftLogDto = new GiftLogDto();
            giftLogDto.setPreviousValue(inventoryItemDto.count);
            inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
            giftLogDto.setGiftLogDto(workingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
            String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
            loggingService.setLogging(userId, 4, log);
            inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
            myGiftInventory.ResetInventoryInfos(inventoryInfosString);

            GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
            responseItemDto.code = inventoryItemDto.code;
            responseItemDto.count = gettingCount;
            changedMyGiftInventoryList.add(responseItemDto);
        }
        /*특정 선물*/
        else if(gettingItemCode.contains("gift_")) {
            List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
            String giftItemCode = gettingItemCode;
            GiftTable giftTable = giftTableList.stream().filter(a -> a.getCode().equals(giftItemCode)).findAny().orElse(null);
            if(giftTable == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: GiftTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: GiftTable not find.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(myGiftInventory == null) {
                myGiftInventory = myGiftInventoryRepository.findByUseridUser(userId).orElse(null);
                if(myGiftInventory == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyGiftInventory not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: MyGiftInventory not find.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = mailGettingItemsResponseDto.getChangedMyGiftInventoryList();
            String inventoryInfosString = myGiftInventory.getInventoryInfos();
            GiftItemDtosList giftItemInventorys = JsonStringHerlper.ReadValueFromJson(inventoryInfosString, GiftItemDtosList.class);

            GiftItemDtosList.GiftItemDto inventoryItemDto = giftItemInventorys.giftItemDtoList.stream()
                    .filter(a -> a.code.equals(giftTable.getCode()))
                    .findAny()
                    .orElse(null);
            if(inventoryItemDto == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find GiftTable", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find GiftTable", ResponseErrorCode.NOT_FIND_DATA);
            }

            GiftLogDto giftLogDto = new GiftLogDto();
            giftLogDto.setPreviousValue(inventoryItemDto.count);
            inventoryItemDto.AddItem(gettingCount ,giftTable.getStackLimit());
            giftLogDto.setGiftLogDto(workingPosition, inventoryItemDto.code,gettingCount, inventoryItemDto.count);
            String log = JsonStringHerlper.WriteValueAsStringFromData(giftLogDto);
            loggingService.setLogging(userId, 4, log);
            inventoryInfosString = JsonStringHerlper.WriteValueAsStringFromData(giftItemInventorys);
            myGiftInventory.ResetInventoryInfos(inventoryInfosString);

            GiftItemDtosList.GiftItemDto responseItemDto = new GiftItemDtosList.GiftItemDto();
            responseItemDto.code = inventoryItemDto.code;
            responseItemDto.count = gettingCount;
            changedMyGiftInventoryList.add(responseItemDto);
        }
        /*모든 케릭터 조각중 하나*/
        else if(gettingItemCode.equals("characterPieceAll")) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            //List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
            int randIndex = (int) MathHelper.Range(0, copyBelongingCharacterPieceTableList.size());
            BelongingCharacterPieceTable selectedCharacterPiece = copyBelongingCharacterPieceTableList.get(randIndex);
            List<BelongingInventoryDto> changedCharacterPieceList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
            if(itemTypeList == null)
                itemTypeList = itemTypeRepository.findAll();
            ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                    .findAny()
                    .orElse(null);
            if(belongingInventoryList == null)
                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == selectedCharacterPiece.getId())
                    .findAny()
                    .orElse(null);

            if(myCharacterPieceItem == null) {
                belongingInventoryLogDto.setPreviousValue(0);
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
                belongingInventoryDto.setId(myCharacterPieceItem.getId());
                belongingInventoryList.add(myCharacterPieceItem);

                changedCharacterPieceList.add(belongingInventoryDto);
            }
            else {
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(gettingCount, selectedCharacterPiece.getStackLimit());
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(selectedCharacterPiece.getId());
                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                String log = JsonStringHerlper.WriteValueAsStringFromData(belongingInventoryLogDto);
                loggingService.setLogging(userId, 3, log);
                belongingInventoryDto.setId(myCharacterPieceItem.getId());
                changedCharacterPieceList.add(belongingInventoryDto);
            }
        }
        /*특정 케릭터 조각*/
        else if(gettingItemCode.contains("characterPiece")) {
            BelongingInventoryLogDto belongingInventoryLogDto = new BelongingInventoryLogDto();
            List<BelongingInventoryDto> changedCharacterPieceList = mailGettingItemsResponseDto.getChangedBelongingInventoryList();
            String characterCode = gettingItemCode;//.replace("characterPiece_", "");
            if(itemTypeList == null)
                itemTypeList = itemTypeRepository.findAll();
            ItemType belongingCharacterPieceItemType = itemTypeList.stream()
                    .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                    .findAny()
                    .orElse(null);

            List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
            BelongingCharacterPieceTable characterPiece = belongingCharacterPieceTableList.stream()
                    .filter(a -> a.getCode().equals(characterCode))
                    .findAny()
                    .orElse(null);
            if(characterPiece == null) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find characterPiece.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);
            }
            if(belongingInventoryList == null)
                belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
            BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                    .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == characterPiece.getId())
                    .findAny()
                    .orElse(null);

            if(myCharacterPieceItem == null) {
                belongingInventoryLogDto.setPreviousValue(0);
                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPiece.getId());
                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                myCharacterPieceItem = belongingInventoryDto.ToEntity();
                myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                belongingInventoryList.add(myCharacterPieceItem);
                belongingInventoryDto.setId(myCharacterPieceItem.getId());

                changedCharacterPieceList.add(belongingInventoryDto);
            }
            else {
                belongingInventoryLogDto.setPreviousValue(myCharacterPieceItem.getCount());
                myCharacterPieceItem.AddItem(gettingCount, characterPiece.getStackLimit());

                BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                belongingInventoryDto.setUseridUser(userId);
                belongingInventoryDto.setItemId(characterPiece.getId());
                belongingInventoryDto.setCount(gettingCount);
                belongingInventoryDto.setItemType(belongingCharacterPieceItemType);
                belongingInventoryDto.setId(myCharacterPieceItem.getId());
                belongingInventoryLogDto.setBelongingInventoryLogDto(workingPosition, myCharacterPieceItem.getId(), myCharacterPieceItem.getItemId(), myCharacterPieceItem.getItemType(), gettingCount, myCharacterPieceItem.getCount());
                changedCharacterPieceList.add(belongingInventoryDto);
            }
        }
        /*모든 장비 중 하나*/
        else if(gettingItemCode.equals("equipmentAll")){
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            //장비
            if(heroEquipmentInventoryList == null)
                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }
            //등급 확률	영웅 70%	 전설 20%	신성 9%	고대 1%
            //품질	D	C	B	A	S	SS	SSS
            //확률	15%	25%	45%	9%	5%	1%	0%
            List<Double> gradeProbabilityList = new ArrayList<>();
            gradeProbabilityList.add(70D);
            gradeProbabilityList.add(20D);
            gradeProbabilityList.add(9D);
            gradeProbabilityList.add(1D);
            String selectedGrade = "";
            int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
            switch (selectedIndex) {
                case 0:
                    selectedGrade = "Hero";
                    break;
                case 1:
                    selectedGrade = "Legend";
                    break;
                case 2:
                    selectedGrade = "Divine";
                    break;
                case 3:
                    selectedGrade = "Ancient";
                    break;
            }

            List<Double> classProbabilityList = new ArrayList<>();
            classProbabilityList.add(15D);
            classProbabilityList.add(25D);
            classProbabilityList.add(45D);
            classProbabilityList.add(9D);
            classProbabilityList.add(5D);
            classProbabilityList.add(1D);
            classProbabilityList.add(0D);
            String selectedClass = "";
            selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
            switch (selectedIndex) {
                case 0:
                    selectedClass = "D";
                    break;
                case 1:
                    selectedClass = "C";
                    break;
                case 2:
                    selectedClass = "B";
                    break;
                case 3:
                    selectedClass = "A";
                    break;
                case 4:
                    selectedClass = "S";
                    break;
                case 5:
                    selectedClass = "SS";
                    break;
                case 6:
                    selectedClass = "SSS";
                    break;
            }
            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);
            List<HeroEquipmentsTable> probabilityList = EquipmentCalculate.GetProbabilityList(heroEquipmentsTableList, EquipmentItemCategory.ALL, selectedGrade);
            int randValue = (int)MathHelper.Range(0, probabilityList.size());
            HeroEquipmentsTable selectEquipment = probabilityList.get(randValue);
            List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
            HeroEquipmentInventory generatedItem = EquipmentCalculate.CreateEquipment(user.getId(), selectEquipment, selectedClass, classValue, optionsInfoTableList);
            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
            equipmentLogDto.setEquipmentLogDto(workingPosition+" - ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
            loggingService.setLogging(userId, 2, log);

            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), generatedItem.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedGrade, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        }
        /*특정 등급, 특정 클래스, 특정 종류의 장비중 하나*/
        else if(gettingItemCode.contains("equipment")) {
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }
            if(heroEquipmentInventoryList == null)
                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }

            List<HeroEquipmentsTable> heroEquipmentsTableList = gameDataTableService.HeroEquipmentsTableList();
            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
            List<EquipmentOptionsInfoTable> optionsInfoTableList = gameDataTableService.OptionsInfoTableList();
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = ApplySomeReward.AddEquipmentItem(user, gettingItemCode, heroEquipmentInventoryList, heroEquipmentInventoryRepository, heroEquipmentsTableList, classValues, optionsInfoTableList, workingPosition, loggingService, errorLoggingService);
            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);

            HeroEquipmentsTable selectedEquipmentItemTable = heroEquipmentsTableList.stream().filter(a -> a.getId() == heroEquipmentInventoryDto.getItem_Id()).findAny().orElse(null);

            /* 업적 : 장비 획득 (특정 품질) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.QUILITY_RESMELT_EQUIPMENT.name(), heroEquipmentInventoryDto.getItemClass(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;
            /* 업적 : 장비 획득 (특정 등급) 미션 체크*/
            changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_EQUIPMENT.name(), selectedEquipmentItemTable.getGrade(), gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        }
        /*인장 중 하나*/
        else if(gettingItemCode.equals("stampAll")){
            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }

            if(heroEquipmentInventoryList == null)
                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }

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
            List<Double> classProbabilityList = new ArrayList<>();
            classProbabilityList.add(15D);
            classProbabilityList.add(25D);
            classProbabilityList.add(45D);
            classProbabilityList.add(9D);
            classProbabilityList.add(5D);
            classProbabilityList.add(1D);
            classProbabilityList.add(0D);
            String selectedClass = "";
            selectedIndex = MathHelper.RandomIndexWidthProbability(classProbabilityList);
            switch (selectedIndex) {
                case 0:
                    selectedClass = "D";
                    break;
                case 1:
                    selectedClass = "C";
                    break;
                case 2:
                    selectedClass = "B";
                    break;
                case 3:
                    selectedClass = "A";
                    break;
                case 4:
                    selectedClass = "S";
                    break;
                case 5:
                    selectedClass = "SS";
                    break;
                case 6:
                    selectedClass = "SSS";
                    break;
            }
            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(selectedClass, classValues);

            HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
            dto.setUseridUser(userId);
            dto.setItem_Id(selectedPassiveItem.getId());
            dto.setItemClassValue(classValue);
            dto.setDecideDefaultAbilityValue(0);
            dto.setDecideSecondAbilityValue(0);
            dto.setLevel(1);
            dto.setMaxLevel(1);
            dto.setExp(0);
            dto.setNextExp(0);
            dto.setItemClass(selectedClass);

            HeroEquipmentInventory generatedItem = dto.ToEntity();
            generatedItem = heroEquipmentInventoryRepository.save(generatedItem);
            HeroEquipmentInventoryDto heroEquipmentInventoryDto = new HeroEquipmentInventoryDto();
            heroEquipmentInventoryDto.InitFromDbData(generatedItem);
            EquipmentLogDto equipmentLogDto = new EquipmentLogDto();
            equipmentLogDto.setEquipmentLogDto(workingPosition+" - ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
            loggingService.setLogging(userId, 2, log);

            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
        }
        /*특정 품질의 인장*/
        else if(gettingItemCode.contains("stamp")){

            if(user == null) {
                user = userRepository.findById(userId).orElse(null);
                if(user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Can't Find user.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: Can't Find user.", ResponseErrorCode.NOT_FIND_DATA);
                }
            }

            if(heroEquipmentInventoryList == null)
                heroEquipmentInventoryList = heroEquipmentInventoryRepository.findByUseridUser(user.getId());

            if(heroEquipmentInventoryList.size() == user.getHeroEquipmentInventoryMaxSlot()) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.FULL_EQUIPMENTINVENTORY.getIntegerValue(), "Fail! -> Cause: Full Inventory!", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Full Inventory!", ResponseErrorCode.FULL_EQUIPMENTINVENTORY);
            }

            String itemClass = "D";
            if(gettingItemCode.contains("ClassD")) {
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

            HeroEquipmentClassProbabilityTable classValues = gameDataTableService.HeroEquipmentClassProbabilityTableList().get(1);
            int classValue = (int) EquipmentCalculate.GetClassValueFromClassCategory(itemClass, classValues);

            HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
            dto.setUseridUser(userId);
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
            equipmentLogDto.setEquipmentLogDto(workingPosition+" - ["+generatedItem.getId()+"]", generatedItem.getId(), "추가", heroEquipmentInventoryDto);
            String log = JsonStringHerlper.WriteValueAsStringFromData(equipmentLogDto);
            loggingService.setLogging(userId, 2, log);

            List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = mailGettingItemsResponseDto.getChangedHeroEquipmentInventoryList();
            changedHeroEquipmentInventoryList.add(heroEquipmentInventoryDto);
        }

        return changedMissionsData;
    }

    public InfiniteRanking setScore(User user , InfiniteRanking userInfiniteRanking, int floor) {

        Long userId = user.getId();

        //팀 정보 찾아서 셋팅
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String teamCharactersIds = myTeamInfo.getInfiniteTowerTeam();
        String[] teamCharactersIdsArray = teamCharactersIds.split(",");
        List<Long> charactersIdsList = new ArrayList<>();
        for(String characterIdStr : teamCharactersIdsArray) {
            if(Strings.isNullOrEmpty(characterIdStr))
                continue;
            long characterId = Long.parseLong(characterIdStr);
            charactersIdsList.add(characterId);
        }
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllById(charactersIdsList);
        String teamCharacterCodes = LeaderboardService.getTeamCharacterCodes(myCharactersList);

        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(myProfileData.getJson_saveDataValue(), ProfileDataDto.class);
        String profileHero = profileDataDto.getProfileHero();
        int profileFrame = profileDataDto.getProfileFrame();
        //Rds 저장 (데이터 원본 및 데이터 분석용)
        if(userInfiniteRanking == null) {
            userInfiniteRanking = InfiniteRanking.builder().useridUser(userId).userGameName(
                    user.getUserGameName()).floor(floor).teamCharactersIds(teamCharactersIds).teamCharacterCodes(teamCharacterCodes).profileHero(profileHero).profileFrame(profileFrame).build();
            infiniteRankingRepository.save(userInfiniteRanking);
        }
        else {
            userInfiniteRanking.refresh(teamCharactersIds, teamCharacterCodes, floor, profileHero, profileFrame);
        }
        //Redis 캐시 저장
        long score = (long)userInfiniteRanking.getFloor();
        InfiniteTowerRedisScore redisScore = infiniteTowerRedisScoreRepository.findById(userId).orElse(null);
        if(redisScore == null) {//redis 에 해당 유저 캐싱 정보 없으므로 만들어줌.
            redisScore = InfiniteTowerRedisScore.builder().id(userId).score(score).userGameName(
                    user.getUserGameName()).teamCharactersIds(teamCharactersIds).teamCharacterCodes(teamCharacterCodes).profileHero(profileHero).profileFrame(profileFrame).build();
            infiniteTowerRedisScoreRepository.save(redisScore);
        }
        else {
            //재캐싱 하였으니 팀정보 및 점수를 포함한 저장시간 리프레쉬.
            redisScore.refresh(teamCharactersIds, teamCharacterCodes, score, profileHero, profileFrame);
            infiniteTowerRedisScoreRepository.save(redisScore);
        }
        //랭킹을 위한 Redis Set 저장.
        //RdsScoreDto rdsScoreDto = new RdsScoreDto(userInfiniteRanking.getId(), userInfiniteRanking.getUseridUser(), nowSeasonId, 1, (long)userInfiniteRanking.getFloor(), userInfiniteRanking.getTeamCharactersIds(), userInfiniteRanking.getTeamCharacterCodes(), userInfiniteRanking.getUserGameName());
        redisLongTemplate.opsForZSet().add(InfinityLeaderboardService.INFINITETOWER_RANKING_LEADERBOARD, userId, score);

        //실제 해당층을 깬 기록이 있는지 확인.
        List<InfiniteTowerRecords> infiniteTowerRecordsList = infinityTowerRecordsRepository.findAll();
        InfiniteTowerRecords infiniteTowerRecord = infiniteTowerRecordsList.stream()
                .filter(a -> a.getFloor() == floor)
                .findAny()
                .orElse(null);
        if(infiniteTowerRecord != null) {
            if(infiniteTowerRecord.getUseridUser().equals(0L)) {
                infiniteTowerRecord.Record(userId, teamCharactersIds, teamCharacterCodes, user.getUserGameName(), profileHero, profileFrame);
            }
        }
        return userInfiniteRanking;
    }
}
