package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.BattlePowerRankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.BattlePowerRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.BattlePowerRedisRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class BattlePowerLeaderboardService {
    public static String BATTLE_POWER_LEADERBOARD = "battle_power_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final BattlePowerRedisRankingRepository battlePowerRedisRankingRepository;
    private final BattlePowerRankingRepository battlePowerRankingRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    public BattlePowerRanking setScore(Long userId, Long battlePower) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        BattlePowerRanking battlePowerRanking = battlePowerRankingRepository.findByUseridUser(userId).orElse(null);
        if(battlePowerRanking == null) {
            battlePowerRanking = BattlePowerRanking.builder().useridUser(userId).battlePower(battlePower).userGameName(user.getUserGameName()).build();
            battlePowerRanking = battlePowerRankingRepository.save(battlePowerRanking);
        }
        else
            battlePowerRanking.refresh(battlePower);
        if(!user.isDummyUser()) {
            BattlePowerRedisRanking battlePowerRedisRanking = battlePowerRedisRankingRepository.findById(userId).orElse(null);
            if(battlePowerRedisRanking == null)
                battlePowerRedisRanking = BattlePowerRedisRanking.builder().id(userId).battlePower(battlePower).userGameName(user.getUserGameName()).build();
            else
                battlePowerRedisRanking.refresh(battlePower);
            battlePowerRedisRankingRepository.save(battlePowerRedisRanking);

            redisLongTemplate.opsForZSet().add(BATTLE_POWER_LEADERBOARD, userId, battlePower);
        }
        battlePowerRanking.SetRanking(getRank(userId).intValue());
        return battlePowerRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(BATTLE_POWER_LEADERBOARD, userId);
        if(myRank == null)
            return 0L;
        myRank = myRank + 1L;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount  = redisLongTemplate.opsForZSet().size(BATTLE_POWER_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser (Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(BATTLE_POWER_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<BattlePowerRankingInfoDto> list = new ArrayList<>();
        int ranking = 1;
        Long tempBattlePower = 0L;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if(ranking > 100)
                break;
            Long id = user.getValue();
            BattlePowerRedisRanking value = battlePowerRedisRankingRepository.findById(id).get();
            if(!tempBattlePower.equals(value.getBattlePower())) {
                tempBattlePower = value.getBattlePower();
                tempRanking = ranking;
            }

            BattlePowerRankingInfoDto battlePowerRankingInfoDto = new BattlePowerRankingInfoDto();
            battlePowerRankingInfoDto.SetBattlePowerRankingInfoDto(id, value.getUserGameName(), tempRanking, tempBattlePower, 0);
            list.add(battlePowerRankingInfoDto);
            ranking++;
        }

        BattlePowerRanking battlePowerRanking = battlePowerRankingRepository.findByUseridUser(userId).orElse(null);
        BattlePowerRankingInfoDto myRankingInfo = new BattlePowerRankingInfoDto();
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(battlePowerRanking != null)
            myRankingInfo.SetBattlePowerRankingInfoDto(userId, user.getUserGameName(), getRank(userId).intValue(), battlePowerRanking.getBattlePower(), getPercent(userId));
        else
            myRankingInfo.SetBattlePowerRankingInfoDto(userId, user.getUserGameName(), 0, 0L, 0);
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
