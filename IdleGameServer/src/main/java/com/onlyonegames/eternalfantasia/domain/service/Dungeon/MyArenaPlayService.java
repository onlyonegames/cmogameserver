package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaPlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaPlayLogForBattleRecordDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaSeasonSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CharacterFatigabilityLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.CurrencyLogDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.EternalPass.EventMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Mission.MissionsDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.MyProfileMissionDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ProfileDto.ProfileDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RdsScoreDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaPlayLogForBattleRecord;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass.MyEternalPassMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Mission.MyMissionsData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyProfileData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyTeamInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaPlayInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ArenaRewardsTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAreanPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaPlayLogForBattleRecordRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.EternalPass.MyEternalPassMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Mission.MyMissionsDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyProfileDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyTeamInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.domain.service.MyProfileService;
import com.onlyonegames.eternalfantasia.etc.Defines;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.eternalfantasia.etc.SystemMailInfos;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyArenaPlayService {
    private final MyArenaPlayLogForBattleRecordRepository myArenaPlayLogForBattleRecordRepository;
    private final MyEternalPassMissionsDataRepository myEternalPassMissionsDataRepository;
    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final MyAreanPlayDataRepository myAreanPlayDataRepository;
    private final MyMissionsDataRepository myMissionsDataRepository;
    private final MyProfileDataRepository myProfileDataRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final MyTeamInfoRepository myTeamInfoRepository;
    private final ErrorLoggingService errorLoggingService;
    private final LeaderboardService leaderboardService;
    private final RdsScoreRepository rdsScoreRepository;
    private final MyMailBoxService myMailBoxService;
    private final MyProfileService myProfileService;
    private final UserRepository userRepository;
    private final LoggingService loggingService;
    public Map<String, Object> ArenaPlayTimeSet(Long userId, Map<String, Object> map) {

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        arenaPlayData.StartStagePlay();
        return map;
    }

    public Map<String, Object> DirectBuyArenaTicket(Long userId, Map<String, Object> map) {

        /*아레나 티켓 구매*/
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        CurrencyLogDto diamondLogDto = new CurrencyLogDto();
        int diaPreviousValue = user.getDiamond();
        int cost = 30;
        if(!user.SpendDiamond(cost)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
        }
        diamondLogDto.setCurrencyLogDto("아레나 티켓 구매", "다이아", diaPreviousValue, -cost, user.getDiamond());
        String diaLog = JsonStringHerlper.WriteValueAsStringFromData(diamondLogDto);
        loggingService.setLogging(userId, 1, diaLog);
        CurrencyLogDto ticketLogDto = new CurrencyLogDto();
        int ticketPreviousValue = user.getArenaTicket();
        if(!user.AddArenaTicket(1)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_GETTING_ARENATICKET_MAX.getIntegerValue(), "Fail -> Cause: Can't getting arenaTicket(MAX)", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail -> Cause: Can't getting arenaTicket(MAX)", ResponseErrorCode.CANT_GETTING_ARENATICKET_MAX);
        }
        ticketLogDto.setCurrencyLogDto("아레나 티켓 구매", "아레나 티켓", ticketPreviousValue, 1, user.getArenaTicket());
        String ticketLog = JsonStringHerlper.WriteValueAsStringFromData(ticketLogDto);
        loggingService.setLogging(userId, 1, ticketLog);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> ArenaPlay(Long userId, Long selecteMatchUserId, int enemyTeamBattlePower, int myTeamBattlePower, Map<String, Object> map) {

        /*자원 체크*/
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }


        ArenaPlayInfoTable arenaPlayInfoTable = gameDataTableService.ArenaPlayInfoTableList().get(0);
        int arenaTicketCost = arenaPlayInfoTable.getNeedCountArenaTicket();
        CurrencyLogDto freeTicketLogDto = new CurrencyLogDto();
        int freeTicketPreviousValue = user.getFreeArenaCountPerDay();
        if(!user.SpendFreeArenaCountPerDay(arenaTicketCost)) {
            CurrencyLogDto ticketLogDto = new CurrencyLogDto();
            int ticketPreviousValue = user.getArenaTicket();
            if(!user.SpendArenaTicket(arenaTicketCost)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_ARENATICKET.getIntegerValue(), "Fail -> Cause: Need More ArenaTicket", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Need More ArenaTicket", ResponseErrorCode.NEED_MORE_ARENATICKET);
            }
            ticketLogDto.setCurrencyLogDto("아레나 진행", "아레나 티켓", ticketPreviousValue, -arenaTicketCost, user.getArenaTicket());
            String log = JsonStringHerlper.WriteValueAsStringFromData(ticketLogDto);
            loggingService.setLogging(userId, 1, log);
        }else {
            freeTicketLogDto.setCurrencyLogDto("아레나 진행", "아레나 무료 티켓", freeTicketPreviousValue, -arenaTicketCost, user.getFreeArenaCountPerDay());
            String log = JsonStringHerlper.WriteValueAsStringFromData(freeTicketLogDto);
            loggingService.setLogging(userId, 1, log);
        }

        //데이터 검증
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(selecteMatchUserId).orElse(null);
        if(rdsScore == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: rdsScore not find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: rdsScore not find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        if(arenaPlayData == null)  {
            MyArenaPlayDataDto newPlayDataDto = new MyArenaPlayDataDto();
            newPlayDataDto.setUseridUser(userId);
            arenaPlayData = myAreanPlayDataRepository.save(newPlayDataDto.ToEntity());
        }

        //매칭 상대 셋팅
        arenaPlayData.SetMatchedUserId(selecteMatchUserId);
        arenaPlayData.SetTeamBattlePower(enemyTeamBattlePower, myTeamBattlePower);

        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        myArenaSeasonSaveData.Play();
        arenaPlayData.StartStagePlay();
        //팀동료 케릭터들 피로도 깍기
        MyTeamInfo myTeamInfo = myTeamInfoRepository.findByUseridUser(userId);
        String[] myTeams = myTeamInfo.getArenaPlayTeam().split(",");
        List<Long> teamIdList = new ArrayList<>();
        for(int i = 0; i < myTeams.length; i++) {
            Long characterId = Long.parseLong(myTeams[i]);
            if(!characterId.equals(0L)) {
                teamIdList.add(characterId);
            }
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

        //메인영웅(code:hero)은 제외
        List<MyCharacters> teamCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        List<Long> characterId = new ArrayList<>();
        List<Integer> previousFatigability = new ArrayList<>();
        List<Integer> presentFatigability = new ArrayList<>();
        int spendFatigability = 10;
        for(MyCharacters myCharacters : teamCharactersList) {
            if(!myCharacters.getCodeHerostable().equals("hero")) {
                Long inTeamHeroId = teamIdList.stream()
                        .filter(a -> a.equals(myCharacters.getId()))
                        .findAny()
                        .orElse(null);
                if(inTeamHeroId != null) {
                    characterId.add(myCharacters.getId());
                    previousFatigability.add(myCharacters.getFatigability());
                    myCharacters.SpendFatigability(spendFatigability);
                    presentFatigability.add(myCharacters.getFatigability());
                    /* 패스 업적 : 피로도 소모 체크*/
                    changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.SPEND_FATIGABILITY.name(),"empty", spendFatigability, gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
                }
            }
        }
        if(!characterId.isEmpty()){
            CharacterFatigabilityLogDto characterFatigabilityLogDto = new CharacterFatigabilityLogDto();
            characterFatigabilityLogDto.setCharacterFatigabilityLogDto("아레나 진행", characterId, previousFatigability, presentFatigability);
            String fatigabilityLog = JsonStringHerlper.WriteValueAsStringFromData(characterFatigabilityLogDto);
            loggingService.setLogging(userId, 5, fatigabilityLog);
        }
        List<MyCharactersBaseDto> teamCharactersDtoList = new ArrayList<>();
        for(MyCharacters temp : teamCharactersList){
            MyCharactersBaseDto myCharactersBaseDto = new MyCharactersBaseDto();
            myCharactersBaseDto.InitFromDbData(temp);
            teamCharactersDtoList.add(myCharactersBaseDto);
        }
        map.put("user", user);
        map.put("myTeamCharacters",teamCharactersDtoList);

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
        /* 업적 : 아레나 도전 미션 체크*/
        changedMissionsData = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ARENA.name(), "empty", gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList()) || changedMissionsData;

        /*업적 : 미션 데이터 변경점 적용*/
        if(changedMissionsData) {
            jsonMyMissionData = JsonStringHerlper.WriteValueAsStringFromData(myMissionsDataDto);
            myMissionsData.ResetSaveDataValue(jsonMyMissionData);
            map.put("exchange_myMissionsData", true);
            map.put("myMissionsDataDto", myMissionsDataDto);
            map.put("lastDailyMissionClearTime", myMissionsData.getLastDailyMissionClearTime());
            map.put("lastWeeklyMissionClearTime", myMissionsData.getLastWeeklyMissionClearTime());
        }

        /* 패스 업적 : 아레나 도전 미션 체크*/
        changedEternalPassMissionsData = myEternalPassMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.PLAY_ARENA.name(),"empty", gameDataTableService.EternalPassDailyMissionTableList(), gameDataTableService.EternalPassWeekMissionTableList(), gameDataTableService.EternalPassQuestMissionTableList()) || changedEternalPassMissionsData;
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

    private Sort sortByBattleEndTime() {
        return Sort.by(Sort.Direction.DESC, "battleEndTime");
    }

    /** 전투 기록실을 위한 로그 추가. 로그는 최대 20개로 20개를 넘어가는 시점에 AddLog 가 호출 되면 가장 오래된것부터 삭제하고 추가한다.*/
    private MyArenaPlayLogForBattleRecord AddLogForBattleRecord(Long userId, Long enemyUserId, int attackOrDefence/**1 attack, 2 defence*/, int winOrDefeat/**1 win, 2 Defeat*/, String enemyUserGameName, int enemyTeamBattlePower, int gettingPoint, int enemyTier, String profileHero, int profileFrame,
                                       LocalDateTime battleStartTime, LocalDateTime battleEndTime, List<MyArenaPlayLogForBattleRecord> myArenaPlayLogForBattleRecordsList, Long nowRanking, Long nowScore, int nowTier, Long previousRanking, Long previousScore, int previousTier) {

        int listSize = 0;
        if(myArenaPlayLogForBattleRecordsList != null)
            listSize = myArenaPlayLogForBattleRecordsList.size();

        while(listSize >= 20) {
            MyArenaPlayLogForBattleRecord removeAbleData = myArenaPlayLogForBattleRecordsList.get(listSize - 1);
            myArenaPlayLogForBattleRecordsList.remove(removeAbleData);
            listSize = myArenaPlayLogForBattleRecordsList.size();
            myArenaPlayLogForBattleRecordRepository.delete(removeAbleData);
        }

        MyArenaPlayLogForBattleRecordDto myArenaPlayLogForBattleRecordDto = new MyArenaPlayLogForBattleRecordDto();
        myArenaPlayLogForBattleRecordDto.setUseridUser(userId);
        myArenaPlayLogForBattleRecordDto.setAttackOrDefence(attackOrDefence);
        myArenaPlayLogForBattleRecordDto.setWinOrDefeat(winOrDefeat);
        myArenaPlayLogForBattleRecordDto.setEnemyUserId(enemyUserId);
        myArenaPlayLogForBattleRecordDto.setEnemyUserGameName(enemyUserGameName);
        myArenaPlayLogForBattleRecordDto.setEnemyTeamBattlePower(enemyTeamBattlePower);
        myArenaPlayLogForBattleRecordDto.setGettingPoint(gettingPoint);
        myArenaPlayLogForBattleRecordDto.setEnemyTier(enemyTier);
        myArenaPlayLogForBattleRecordDto.setNewLog(true);
        myArenaPlayLogForBattleRecordDto.setBattleStartTime(battleStartTime);
        myArenaPlayLogForBattleRecordDto.setBattleEndTime(battleEndTime);
        myArenaPlayLogForBattleRecordDto.setNowRanking(nowRanking);
        myArenaPlayLogForBattleRecordDto.setNowScore(nowScore);
        myArenaPlayLogForBattleRecordDto.setNowTier(nowTier);
        myArenaPlayLogForBattleRecordDto.setPreviousRanking(previousRanking);
        myArenaPlayLogForBattleRecordDto.setPreviousScore(previousScore);
        myArenaPlayLogForBattleRecordDto.setPreviousTier(previousTier);
        myArenaPlayLogForBattleRecordDto.setProfileFrame(profileFrame);
        myArenaPlayLogForBattleRecordDto.setProfileHero(profileHero);
        MyArenaPlayLogForBattleRecord myArenaPlayLogForBattleRecord = myArenaPlayLogForBattleRecordDto.ToEntity();
        return myArenaPlayLogForBattleRecord;
    }

    //아레나 승리
    public Map<String, Object> ArenaWin(Long userId, Map<String, Object> map) {

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        arenaPlayData.ClearStagePlay();
        RdsScore matchedUserRdsScore = rdsScoreRepository.findByUseridUser(arenaPlayData.getMatchedUser()).orElse(null);
        if(matchedUserRdsScore == null) {
            errorLoggingService.SetErrorLog(arenaPlayData.getMatchedUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MatchedUserRdsScore not find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MatchedUserRdsScore not find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }
        Long matchedUserScore = matchedUserRdsScore.getScore();

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        User enemyUser = userRepository.findById(matchedUserRdsScore.getUseridUser()).orElse(null);
        if(enemyUser == null) {
            errorLoggingService.SetErrorLog(matchedUserRdsScore.getUseridUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        Long userScore = 0L;
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(userId).orElse(null);

        int myPreviousTier = gameDataTableService.RankingTierTableList().size();
        if(rdsScore != null) {
            userScore = rdsScore.getScore();
            myPreviousTier = rdsScore.getRankingtiertableId();
        }
        Long previousScore = userScore;
        Long previousEnemyScore = matchedUserScore;
        int matchedUserTier = matchedUserRdsScore.getRankingtiertableId();

        if(myPreviousTier < matchedUserTier) {
            userScore += 1;
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore -= 2;
        }
        else if(myPreviousTier == matchedUserTier) {
            userScore += 2;
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore -= 3;
        }
        else if(myPreviousTier > matchedUserTier) {
            userScore += 3;
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore -= 4;
        }

        userScore = MathHelper.Clamp(userScore, 0L, 9999999999999L);
        matchedUserScore =  MathHelper.Clamp(matchedUserScore, 0L, 9999999999999L);

        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        Long previousRanking = myArenaSeasonSaveData.getSeasonRank();
        
        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;

        //공격자 정보 셋팅
        RdsScore savedScore = leaderboardService.setScore(user.getId(), userScore);
        Long changedRanking = leaderboardService.getRank(savedScore.getUseridUser());
        myArenaSeasonSaveData.Win(savedScore.getRankingtiertableId(), userScore, changedRanking);
        //디펜스 정보 셋팅
        MyArenaSeasonSaveData enemyArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(enemyUser.getId()).orElse(null);
        if(enemyArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(enemyUser.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        Long enemyPreviousRanking = enemyArenaSeasonSaveData.getSeasonRank();
        int enemyPreviousTier = matchedUserRdsScore.getRankingtiertableId();
        RdsScore enemySaveData = leaderboardService.setScore(enemyUser.getId(), matchedUserScore);
        Long enemyChangedRanking = leaderboardService.getRank(enemySaveData.getUseridUser());
        enemyArenaSeasonSaveData.Defeat(enemySaveData.getRankingtiertableId(), matchedUserScore, enemyChangedRanking);

        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();
        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
                .findAny()
                .orElse(null);

        if(arenaRewardsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardInfoArray = arenaRewardsTable.getPlayWinReward().split(":");
        int rewardArenaCoin = Integer.parseInt(rewardInfoArray[1]);
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getArenaCoin();
        user.AddArenaCoin(rewardArenaCoin);
        currencyLogDto.setCurrencyLogDto("아레나 보상 - 승리", "아레나 코인", previousValue, rewardArenaCoin, user.getArenaCoin());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);

        myArenaSeasonSaveData.ResetPreviousTierId(myPreviousTier);
        myArenaSeasonSaveData.ResetChangedTierId(savedScore.getRankingtiertableId());
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("playWinReward", rewardArenaCoin);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);

        map.put("previousScore", previousScore);
        map.put("changedScore", userScore);

        map.put("previousRanking", previousRanking);
        map.put("changedRanking", changedRanking);


        /* 프로필 프레임 획득 체크 */
        Long enemyUserId = enemyUser.getId();
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(enemyUserId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String json_missionData = myProfileData.getJson_missionData();
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        MyProfileMissionDataDto myProfileMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_missionData, MyProfileMissionDataDto.class);
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);
        boolean changedProfileMissionData = false;

        String enemyJson_saveDataValue = myProfileData.getJson_saveDataValue();
        ProfileDataDto enemyProfileDataDto = JsonStringHerlper.ReadValueFromJson(enemyJson_saveDataValue, ProfileDataDto.class);
        myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }


        //전투 기록실을 위한 로그 추가.(내 기록실)
        List<MyArenaPlayLogForBattleRecord> myArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(userId, sortByBattleEndTime());
        MyArenaPlayLogForBattleRecord newMyArenaPlayLogForBattleRecord = AddLogForBattleRecord(userId, enemyUser.getId(), Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_ATTACK, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_WIN, enemyUser.getUserGameName(),
                arenaPlayData.getEnemyTeamBattlePower(), (int)(userScore - previousScore), enemyPreviousTier, enemyProfileDataDto.getProfileHero(), enemyProfileDataDto.getProfileFrame(), arenaPlayData.getBattleStartTime(), arenaPlayData.getBattleEndTime(), myArenaPlayLogForBattleRecordsList, changedRanking, userScore, savedScore.getRankingtiertableId(), previousRanking, previousScore, myPreviousTier);

        newMyArenaPlayLogForBattleRecord =  myArenaPlayLogForBattleRecordRepository.save(newMyArenaPlayLogForBattleRecord);
        myArenaPlayLogForBattleRecordsList.add(newMyArenaPlayLogForBattleRecord);
        myArenaPlayLogForBattleRecordsList.sort((a,b) -> b.getBattleEndTime().compareTo(a.getBattleEndTime()));
        List<MyArenaPlayLogForBattleRecordDto> myArenaPlayLogForBattleRecordDtoList = new ArrayList<>();
        for(MyArenaPlayLogForBattleRecord temp : myArenaPlayLogForBattleRecordsList){
            MyArenaPlayLogForBattleRecordDto myArenaPlayLogForBattleRecordDto = new MyArenaPlayLogForBattleRecordDto();
            myArenaPlayLogForBattleRecordDto.InitFromDbData(temp);
            myArenaPlayLogForBattleRecordDtoList.add(myArenaPlayLogForBattleRecordDto);
        }
        map.put("myArenaPlayLogForBattleRecordsList", myArenaPlayLogForBattleRecordDtoList);

        //전투 기록실을 위한 로그 추가.(적 기록실)
        List<MyArenaPlayLogForBattleRecord> enemyArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(enemyUserId, sortByBattleEndTime());
        MyArenaPlayLogForBattleRecord newEnemyArenaPlayLogForBattleRecord = AddLogForBattleRecord(enemyUserId, userId, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFENCE, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFEAT, user.getUserGameName(),
                arenaPlayData.getMyTeamBattlePower(), (int)(matchedUserScore - previousEnemyScore), myPreviousTier, profileDataDto.getProfileHero(), profileDataDto.getProfileFrame(), arenaPlayData.getBattleStartTime(), arenaPlayData.getBattleEndTime(), enemyArenaPlayLogForBattleRecordsList, enemyChangedRanking, matchedUserScore, enemySaveData.getRankingtiertableId(), enemyPreviousRanking, previousEnemyScore, enemyPreviousTier);
        myArenaPlayLogForBattleRecordRepository.save(newEnemyArenaPlayLogForBattleRecord);


        RankingTierTable myHighestTier = gameDataTableService.RankingTierTableList().stream().filter(i -> i.getRankingtiertable_id() == myArenaSeasonSaveData.getHighestRankingtiertable_id()).findAny().orElse(null);
        if(myHighestTier == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: RankingTierTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: RankingTierTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myArenaSeasonSaveDataDto.isReceiveableTierUpReward()){

            changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), myHighestTier.getRankGrade(), gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
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
        /* 업적 : 아레나 점수 달성 체크*/
        StringMaker.Clear();
        List<String> pointParams = new ArrayList<>();
        int alreadyOpenStepMax = 0;
        for(String pointParam : MissionsDataDto.MISSION_PARAM.ARENA_POINT.getParams()){
            String onlyPointStr = pointParam.replace("pt","");
            int point = Integer.parseInt(onlyPointStr);
            if(point > userScore){
                break;
            }
            alreadyOpenStepMax++;
            pointParams.add(pointParam);
        }

        for(String pointParam : pointParams){
            boolean checkedTrue = myMissionsDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_POINT.name(), pointParam, gameDataTableService.DailyMissionTableList(), gameDataTableService.WeeklyMissionTableList(), gameDataTableService.QuestMissionTableList());
            if(checkedTrue) {
                changedMissionsData = changedMissionsData || checkedTrue;
                break;
            }
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

        if(changedProfileMissionData) {
            myProfileService.GetProfileFrame(userId, myProfileMissionDataDto, profileDataDto);
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);
            json_missionData = JsonStringHerlper.WriteValueAsStringFromData(myProfileMissionDataDto);
            myProfileData.ResetJson_saveDataValue(json_saveDataValue);
            myProfileData.ResetJson_missionData(json_missionData);
            map.put("myProfileDataDto", profileDataDto);
            map.put("myProfileMissionDataDto", myProfileMissionDataDto);
        }

        return map;
    }

    //아레나 패배
    public Map<String, Object> ArenaFail(Long userId, Map<String, Object> map) {

        MyArenaPlayData arenaPlayData = myAreanPlayDataRepository.findByUseridUser(userId);
        arenaPlayData.DefeatStagePlay();

        RdsScore matchedUserRdsScore = rdsScoreRepository.findByUseridUser(arenaPlayData.getMatchedUser()).orElse(null);
        if(matchedUserRdsScore == null) {
            errorLoggingService.SetErrorLog(arenaPlayData.getMatchedUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MatchedUserRdsScore not find. userId => " + arenaPlayData.getMatchedUser(), this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MatchedUserRdsScore not find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }
        Long matchedUserScore = matchedUserRdsScore.getScore();

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        User enemyUser = userRepository.findById(matchedUserRdsScore.getUseridUser()).orElseThrow(
                () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));
        if(enemyUser == null) {
            errorLoggingService.SetErrorLog(matchedUserRdsScore.getUseridUser(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        Long userScore = 0L;
        int myPreviousTier = gameDataTableService.RankingTierTableList().size();
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(userId).orElse(null);
        if(rdsScore != null) {
            userScore = rdsScore.getScore();
            myPreviousTier = rdsScore.getRankingtiertableId();
        }
        Long previousScore = userScore;
        Long previousEnemyScore = matchedUserScore;
        int matchedUserTier = matchedUserRdsScore.getRankingtiertableId();
        if(myPreviousTier < matchedUserTier) {
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore += 1;
            userScore -= 2;
        }
        else if(myPreviousTier == matchedUserTier) {
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore += 2;
            userScore -= 3;

        }
        else if(myPreviousTier > matchedUserTier) {
            if(!matchedUserRdsScore.isDummyUser())
                matchedUserScore += 3;
            userScore -= 4;
        }


        userScore = MathHelper.Clamp(userScore, 0L, 9999999999999L);
        matchedUserScore =  MathHelper.Clamp(matchedUserScore, 0L, 9999999999999L);

        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }

        int previousTier = myArenaSeasonSaveData.previousTierId;
        Long previousRanking = myArenaSeasonSaveData.getSeasonRank();

        //최신 시즌 정보 아이디
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int nowSeasonId = (int)seasonInfosCount;

        //공격자 정보 셋팅
        RdsScore savedScore = leaderboardService.setScore(user.getId(), userScore);
        Long changedRanking = leaderboardService.getRank(savedScore.getUseridUser());
        myArenaSeasonSaveData.Defeat(savedScore.getRankingtiertableId(), userScore, changedRanking);

        //디펜스 정보 셋팅
        MyArenaSeasonSaveData enemyArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(enemyUser.getId()).orElseThrow(
                () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));
        if(enemyArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(enemyUser.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaSeasonSaveData not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        Long enemyPreviousRanking = enemyArenaSeasonSaveData.getSeasonRank();
        int enemyPreviousTier = matchedUserRdsScore.getRankingtiertableId();
        RdsScore enemySaveData = leaderboardService.setScore(enemyUser.getId(), matchedUserScore);
        Long enemyChangedRanking = leaderboardService.getRank(enemySaveData.getUseridUser());
        int previousHighestTier_id = enemyArenaSeasonSaveData.getHighestRankingtiertable_id();
        boolean previousReceiveableTierUpReward = enemyArenaSeasonSaveData.isReceiveableTierUpReward();
        enemyArenaSeasonSaveData.Win(enemySaveData.getRankingtiertableId(), matchedUserScore, enemyChangedRanking);
        if(enemyArenaSeasonSaveData.getHighestRankingtiertable_id()<previousHighestTier_id&&previousReceiveableTierUpReward){
            previousReward(enemyUser.getId(), enemyArenaSeasonSaveData, enemyUser);
        }


        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();
        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id())
                .findAny()
                .orElse(null);

        if(arenaRewardsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        String[] rewardInfoArray = arenaRewardsTable.getPlayDefeatReward().split(":");
        int rewardArenaTicketCount = Integer.parseInt(rewardInfoArray[1]);
        CurrencyLogDto currencyLogDto = new CurrencyLogDto();
        int previousValue = user.getArenaCoin();
        user.AddArenaCoin(rewardArenaTicketCount);
        currencyLogDto.setCurrencyLogDto("아레나 보상 - 패배", "아레나 코인", previousValue, rewardArenaTicketCount, user.getArenaCoin());
        String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
        loggingService.setLogging(userId, 1, log);
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("playDefeatReward", rewardArenaTicketCount);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);

        map.put("previousScore", previousScore);
        map.put("changedScore", userScore);

        map.put("previousTier", previousTier);
        map.put("changedTier", savedScore.getRankingtiertableId());

        map.put("previousRanking", previousRanking);
        map.put("changedRanking", changedRanking);

        MyProfileData enemyProfileData = myProfileDataRepository.findByUseridUser(enemyUser.getId()).orElse(null);
        if(enemyProfileData == null) {
            errorLoggingService.SetErrorLog(enemyUser.getId(), ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String enemyJson_saveDataValue = enemyProfileData.getJson_saveDataValue();
        ProfileDataDto enemyProfileDataDto = JsonStringHerlper.ReadValueFromJson(enemyJson_saveDataValue, ProfileDataDto.class);

        /* 프로필 프레임 */
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);

        //전투 기록실을 위한 로그 추가.(내 기록실)
        List<MyArenaPlayLogForBattleRecord> myArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(userId, sortByBattleEndTime());
        MyArenaPlayLogForBattleRecord newMyArenaPlayLogForBattleRecord = AddLogForBattleRecord(userId, enemyUser.getId(), Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_ATTACK, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFEAT, enemyUser.getUserGameName(),
                arenaPlayData.getEnemyTeamBattlePower(), (int)(userScore - previousScore), enemyPreviousTier, enemyProfileDataDto.getProfileHero(), enemyProfileDataDto.getProfileFrame(), arenaPlayData.getBattleStartTime(), arenaPlayData.getBattleEndTime(), myArenaPlayLogForBattleRecordsList, changedRanking, userScore, savedScore.getRankingtiertableId(), previousRanking, previousScore, myPreviousTier);
        newMyArenaPlayLogForBattleRecord =  myArenaPlayLogForBattleRecordRepository.save(newMyArenaPlayLogForBattleRecord);
        myArenaPlayLogForBattleRecordsList.add(newMyArenaPlayLogForBattleRecord);
        List<MyArenaPlayLogForBattleRecordDto> myArenaPlayLogForBattleRecordDtoList = new ArrayList<>();
        for(MyArenaPlayLogForBattleRecord temp : myArenaPlayLogForBattleRecordsList){
            MyArenaPlayLogForBattleRecordDto myArenaPlayLogForBattleRecordDto = new MyArenaPlayLogForBattleRecordDto();
            myArenaPlayLogForBattleRecordDto.InitFromDbData(temp);
            myArenaPlayLogForBattleRecordDtoList.add(myArenaPlayLogForBattleRecordDto);
        }
        map.put("myArenaPlayLogForBattleRecordsList", myArenaPlayLogForBattleRecordDtoList);



        //전투 기록실을 위한 로그 추가.(적 기록실)
        Long enemyUserId = enemyUser.getId();
        List<MyArenaPlayLogForBattleRecord> enemyArenaPlayLogForBattleRecordsList = myArenaPlayLogForBattleRecordRepository.findAllByUseridUser(enemyUserId, sortByBattleEndTime());
        MyArenaPlayLogForBattleRecord newEnemyArenaPlayLogForBattleRecord = AddLogForBattleRecord(enemyUser.getId(), userId, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_DEFENCE, Defines.MY_ARENA_PLAY_LOG_FOR_BATTLE_RECORD_WIN, user.getUserGameName(),
                arenaPlayData.getMyTeamBattlePower(), (int)(matchedUserScore - previousEnemyScore), myPreviousTier, profileDataDto.getProfileHero(), profileDataDto.getProfileFrame(), arenaPlayData.getBattleStartTime(), arenaPlayData.getBattleEndTime(), enemyArenaPlayLogForBattleRecordsList, enemyChangedRanking, matchedUserScore, enemySaveData.getRankingtiertableId(), enemyPreviousRanking, previousEnemyScore, enemyPreviousTier);
        myArenaPlayLogForBattleRecordRepository.save(newEnemyArenaPlayLogForBattleRecord);

        return map;
    }

    void previousReward(Long userId, MyArenaSeasonSaveData myArenaSeasonSaveData, User user) {
        List<ArenaRewardsTable> arenaRewardsTableList = gameDataTableService.ArenaRewardsTableList();

        ArenaRewardsTable arenaRewardsTable = arenaRewardsTableList.stream()
                .filter(a -> a.getArenarewardstable_id() == myArenaSeasonSaveData.getRankingtiertable_id()+1)
                .findAny()
                .orElse(null);
        if(arenaRewardsTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: arenaRewardsTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: arenaRewardsTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        RankingTierTable rankingTierTable = gameDataTableService.RankingTierTableList().stream().filter(i -> i.getRankingtiertable_id() == myArenaSeasonSaveData.getHighestRankingtiertable_id()+1).findAny().orElse(null);
        if(rankingTierTable == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: RankingTierTable not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: RankingTierTable not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String[] rewardsArray = arenaRewardsTable.getLeague().split(",");
        List<MailSendRequestDto.Item> itemList = new ArrayList<>();
        for (String reward:rewardsArray){
            String[] splitTemp = reward.split(":");
            MailSendRequestDto.Item item = new MailSendRequestDto.Item();
            item.setItem(splitTemp[0], Integer.parseInt(splitTemp[1]));
            itemList.add(item);
        }

        Map<String, Object> fakeMap = new HashMap<>();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        StringMaker.Clear();
        StringMaker.stringBuilder.append("최초 ");
        StringMaker.stringBuilder.append(rankingTierTable.getGradeName());
        StringMaker.stringBuilder.append(" 달성 보상");
        String title = StringMaker.stringBuilder.toString();
        mailSendRequestDto.SetMailSendRequestDto(title, 10000L, userId, SystemMailInfos.ARENA_SEASON_REWARD_TITLE, itemList, 1, LocalDateTime.now().plusYears(1));
        myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
//        for(int i = 0; i < rewardsArray.length; i++) {
//            String rewardInfos = rewardsArray[i];
//            String[] rewardInfoArray = rewardInfos.split(":");
//            String rewardInfo = rewardInfoArray[0];
//            int gettingCount = Integer.parseInt(rewardInfoArray[1]);
//            if(rewardInfo.equals("diamond")) {
//                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                int previousValue = user.getDiamond();
//                user.AddDiamond(gettingCount);
//                currencyLogDto.setCurrencyLogDto("등급 상승보상 - "+myArenaSeasonSaveData.getRankingtiertable_id()+"티어", "다이아", previousValue, gettingCount, user.getDiamond());
//                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                loggingService.setLogging(userId, 1, log);
//            }
//            else if(rewardInfo.equals("arenaCoin")) {
//                CurrencyLogDto currencyLogDto = new CurrencyLogDto();
//                int previousValue = user.getArenaCoin();
//                user.AddArenaCoin(gettingCount);
//                currencyLogDto.setCurrencyLogDto("등급 상승보상 - "+myArenaSeasonSaveData.getRankingtiertable_id()+"티어", "아레나 코인", previousValue, gettingCount, user.getArenaCoin());
//                String log = JsonStringHerlper.WriteValueAsStringFromData(currencyLogDto);
//                loggingService.setLogging(userId, 1, log);
//            }
//        }

        /*프로필 프레임 획득 체크 준비*/
        MyProfileData myProfileData = myProfileDataRepository.findByUseridUser(userId).orElse(null);
        if(myProfileData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyProfileData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyProfileData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        String json_missionData = myProfileData.getJson_missionData();
        String json_saveDataValue = myProfileData.getJson_saveDataValue();
        MyProfileMissionDataDto myProfileMissionDataDto = JsonStringHerlper.ReadValueFromJson(json_missionData, MyProfileMissionDataDto.class);
        ProfileDataDto profileDataDto = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ProfileDataDto.class);
        boolean changedProfileMissionData = false;
        changedProfileMissionData = myProfileMissionDataDto.CheckMission(MissionsDataDto.MISSION_TYPE.GETTING_ARENA_TIER.name(), rankingTierTable.getRankGrade(), gameDataTableService.ProfileFrameMissionTableList()) || changedProfileMissionData;
        if(changedProfileMissionData) {
            myProfileService.GetProfileFrame(userId, myProfileMissionDataDto, profileDataDto);
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(profileDataDto);
            json_missionData = JsonStringHerlper.WriteValueAsStringFromData(myProfileMissionDataDto);
            myProfileData.ResetJson_saveDataValue(json_saveDataValue);
            myProfileData.ResetJson_missionData(json_missionData);
        }
    }
}
