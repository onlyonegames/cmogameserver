package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousBattlePowerRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousStageRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousWorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyDayRewardDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.*;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;
import static com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService.ARENA_RANKING_LEADERBOARD;
import static com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.BattlePowerLeaderboardService.BATTLE_POWER_LEADERBOARD;
import static com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.StageLeaderboardService.STAGE_RANKING_LEADERBOARD;
import static com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD;

@Service
@Transactional
@AllArgsConstructor
public class StandardTimeResetService {
    private final StandardTimeRepository standardTimeRepository;
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final PreviousArenaRankingRepository previousArenaRankingRepository;
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final PreviousWorldBossRankingRepository previousWorldBossRankingRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final PreviousStageRankingRepository previousStageRankingRepository;
    private final PreviousBattlePowerRankingRepository previousBattlePowerRankingRepository;
    private final MyPassDataRepository myPassDataRepository;
    private final MyShopInfoRepository myShopInfoRepository;
    private final MyGachaInfoRepository myGachaInfoRepository;
    private final ErrorLoggingService errorLoggingService;
    private final ArenaRedisRankingRepository arenaRedisRankingRepository;
    private final StageRedisRankingRepository stageRedisRankingRepository;
    private final BattlePowerRedisRankingRepository battlePowerRedisRankingRepository;
    private final WorldBossRedisRankingRepository worldBossRedisRankingRepository;

    public Map<String, Object> CheckTime(Map<String, Object> map) {
        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        if(standardTime == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found StandardTime", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found StandardTime", ResponseErrorCode.NOT_FIND_DATA);
        }

        LocalDateTime now = LocalDateTime.now();
        boolean day = false;
        boolean week = false;
        boolean month = false;

        if(standardTime.getBaseDayTime().isBefore(now)){
            //TODO 일별 컨텐츠 업데이트 코드 또는 서비스 추가
            ResetWorldBossRanking(); //시스템
//            ResetArenaForDay();//유저
//            ResetDayPass();//유저
//            ResetMyGachaInfo();//유저
            standardTime.SetBaseDayTime();//시스템
            ResetChallengeTower(standardTime);//시스템
            day = true;
        }
        if(standardTime.getBaseWeekTime().isBefore(now)){
            //TODO 주별 컨텐츠 업데이트 코드 또는 서비스 추가
            SetPreviousArenaRanking();//시스템
            SetPreviousStageRanking();//시스템
            SetPreviousBattlePowerRanking();//시스템
            standardTime.SetBaseWeekTime();//시스템
            week = true;
        }
        if(standardTime.getBaseMonthTime().isBefore(now)){
            //TODO 월별 컨텐츠 업데이트 코드 또는 서비스 추가
            standardTime.SetBaseMonthTime();
            month = true;
        }
//        if (day || week || month)
//            ResetShopPurchaseCount(day, week, month);//유저
        return map;
    }

    private void ResetChallengeTower(StandardTime standardTime) {
        int classIndex = standardTime.getChallengeTowerClassIndex() + 1;
        standardTime.SetChallengeTowerClassIndex(classIndex<5?classIndex:0);
//        List<MyContentsInfo> myContentsInfoList = myContentsInfoRepository.findAllByChallengeTowerFloorNot(0);
//        for(MyContentsInfo temp : myContentsInfoList) {
//            temp.SetChallengeTowerFloor(0);
//        }
    }

    private void ResetArenaForDay() {
        List<MyArenaPlayData> myArenaPlayDataList = myArenaPlayDataRepository.findAllByResetAbleMatchingUserOrReMatchingAbleCountNot(false, 5);
        for(MyArenaPlayData temp : myArenaPlayDataList) {
            temp.ResetReMatchingAbleCount();
            temp.ResetPlayableCount();
        }
    }

    private void ResetWorldBossRanking() {
        Long size = redisLongTemplate.opsForZSet().size(WORLD_BOSS_RANKING_LEADERBOARD);
        if (size == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "WORLD_BOSS_RANKING_LEADERBOARD", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("WORLD_BOSS_RANKING_LEADERBOARD", ResponseErrorCode.NOT_FIND_DATA);
        }
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(WORLD_BOSS_RANKING_LEADERBOARD, 0, size)).orElse(Collections.emptySet());
        List<WorldBossRanking> worldBossRankingList = worldBossRankingRepository.findAll();
        previousWorldBossRankingRepository.deleteAll();

        int ranking = 1;
        double tempPoint = 0;
        int tempRanking = 0;

        List<PreviousWorldBossRanking> saveList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            Long id = user.getValue();
            WorldBossRanking worldBossRanking = worldBossRankingList.stream().filter(i -> i.getUseridUser().equals(id)).findAny().orElse(null);
            if (worldBossRanking == null) {
                ranking++;
                continue;
            }
            worldBossRanking.ResetZero();
            WorldBossRedisRanking value = worldBossRedisRankingRepository.findById(id).get();
            if (tempPoint != value.getTotalDamage()) {
                tempPoint = value.getTotalDamage();
                tempRanking = ranking;
            }
            PreviousWorldBossRankingDto previousWorldBossRankingDto = new PreviousWorldBossRankingDto();
            previousWorldBossRankingDto.InitFromRedisData(value, worldBossRanking, tempRanking);
            saveList.add(previousWorldBossRankingDto.ToEntity());
            ranking++;
            worldBossRankingList.remove(worldBossRanking);
        }
        previousWorldBossRankingRepository.saveAll(saveList);
        redisLongTemplate.opsForZSet().getOperations().delete(WORLD_BOSS_RANKING_LEADERBOARD);
//        List<MyWorldBossPlayData> myWorldBossPlayDataList = myWorldBossPlayDataRepository.findAll();
//        for(MyWorldBossPlayData temp : myWorldBossPlayDataList) {
//            temp.ResetPlayableCount();
//        }
    }

    private void SetPreviousArenaRanking() {
        Long size = redisLongTemplate.opsForZSet().size(ARENA_RANKING_LEADERBOARD);
        if (size == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "ARENA_RANKING_LEADERBOARD", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("ARENA_RANKING_LEADERBOARD", ResponseErrorCode.NOT_FIND_DATA);
        }
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(ARENA_RANKING_LEADERBOARD, 0, size)).orElse(Collections.emptySet());
        previousArenaRankingRepository.deleteAll();

        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;

        List<PreviousArenaRanking> saveList = new ArrayList<>();
        for(ZSetOperations.TypedTuple<Long> user : rankings) {
            Long id = user.getValue();
            ArenaRedisRanking value = arenaRedisRankingRepository.findById(id).get();
            if(tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }
            PreviousArenaRankingDto previousArenaRankingDto = new PreviousArenaRankingDto();
            previousArenaRankingDto.InitFromRedisData(value, tempRanking);
            saveList.add(previousArenaRankingDto.ToEntity());
            ranking++;
        }
        previousArenaRankingRepository.saveAll(saveList);
    }

    private void SetPreviousStageRanking() {
        Long size = redisLongTemplate.opsForZSet().size(STAGE_RANKING_LEADERBOARD);
        if (size == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "STAGE_RANKING_LEADERBOARD", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("STAGE_RANKING_LEADERBOARD", ResponseErrorCode.NOT_FIND_DATA);
        }
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(STAGE_RANKING_LEADERBOARD, 0, size)).orElse(Collections.emptySet());
        previousStageRankingRepository.deleteAll();

        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;

        List<PreviousStageRanking> saveList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            Long id = user.getValue();
            StageRedisRanking value = stageRedisRankingRepository.findById(id).get();
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }
            PreviousStageRankingDto previousStageRankingDto = new PreviousStageRankingDto();
            previousStageRankingDto.InitFromRedisData(value, tempRanking);
            saveList.add(previousStageRankingDto.ToEntity());
            ranking++;
        }
        previousStageRankingRepository.saveAll(saveList);
    }

    private void SetPreviousBattlePowerRanking() {
        Long size = redisLongTemplate.opsForZSet().size(BATTLE_POWER_LEADERBOARD);
        if (size == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "BATTLE_POWER_LEADERBOARD", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("BATTLE_POWER_LEADERBOARD", ResponseErrorCode.NOT_FIND_DATA);
        }
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(BATTLE_POWER_LEADERBOARD, 0, size)).orElse(Collections.emptySet());
        previousBattlePowerRankingRepository.deleteAll();

        int ranking = 1;
        double tempBattlePower = 0d;
        int tempRanking = 0;
        List<PreviousBattlePowerRanking> saveList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            Long id = user.getValue();
            BattlePowerRedisRanking value = battlePowerRedisRankingRepository.findById(id).get();
            if (tempBattlePower != value.getBattlePower()) {
                tempBattlePower = value.getBattlePower();
                tempRanking = ranking;
            }
            PreviousBattlePowerRankingDto previousBattlePowerRankingDto = new PreviousBattlePowerRankingDto();
            previousBattlePowerRankingDto.InitFromRedisData(value, tempRanking);
            saveList.add(previousBattlePowerRankingDto.ToEntity());
            ranking++;
        }
        previousBattlePowerRankingRepository.saveAll(saveList);
    }

    private void ResetDayPass() {
        List<MyPassData> myPassDataList = myPassDataRepository.findAll();
        for (MyPassData temp : myPassDataList) {
            MyDayRewardDataJsonDto myDayRewardDataJsonDto = JsonStringHerlper.ReadValueFromJson(temp.getJson_daySaveData(), MyDayRewardDataJsonDto.class);
            myDayRewardDataJsonDto.Init();
            String json_day = JsonStringHerlper.WriteValueAsStringFromData(myDayRewardDataJsonDto);
            temp.ResetDayJsonData(json_day);
            temp.SetGettingCount();
        }
    }
    private void ResetShopPurchaseCount(boolean day, boolean week, boolean month) {
        List<MyShopInfo> myShopInfoList = myShopInfoRepository.findAll();
        for (MyShopInfo temp : myShopInfoList) {
            if (day)
                temp.RechargeDay();
            if (week)
                temp.RechargeWeek();
            if (month)
                temp.RechargeMonth();
        }
    }
    private void ResetMyGachaInfo() {
        List<MyGachaInfo> myGachaInfoList = myGachaInfoRepository.findAll();
        for (MyGachaInfo temp : myGachaInfoList) {
            temp.ResetADCount();
        }
    }
}
