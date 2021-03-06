package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.WorldBossRankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRedisRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.*;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@RequiredArgsConstructor
public class WorldBossLeaderboardService {
    public static String WORLD_BOSS_RANKING_LEADERBOARD = "world_boss_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final WorldBossRedisRankingRepository worldBossRedisRankingRepository;
    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;

    private final WorldBossRankingRepository worldBossRankingRepository;

    public WorldBossRanking setScore(Long userId, double totalDamage, double baseDamage) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        WorldBossRanking worldBossRanking = worldBossRankingRepository.findByUseridUser(userId).orElse(null);
        if(worldBossRanking == null) {
            worldBossRanking = WorldBossRanking.builder().useridUser(userId).userGameName(user.getUserGameName()).totalDamage(totalDamage).bestDamage(baseDamage).build();
            worldBossRanking = worldBossRankingRepository.save(worldBossRanking);
        }
        else {
            worldBossRanking.refresh(totalDamage);
            worldBossRanking.ResetBestDamage(baseDamage);
            worldBossRanking.ResetUserGameName(user.getUserGameName());
        }
        if(!user.isDummyUser()) {
            WorldBossRedisRanking worldBossRedisRanking = worldBossRedisRankingRepository.findById(userId).orElse(null);
            if(worldBossRedisRanking == null)
                worldBossRedisRanking = WorldBossRedisRanking.builder().id(userId).totalDamage(totalDamage).userGameName(user.getUserGameName()).build();
            else {
                worldBossRedisRanking.refresh(totalDamage);
                worldBossRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            worldBossRedisRankingRepository.save(worldBossRedisRanking);

            redisLongTemplate.opsForZSet().add(WORLD_BOSS_RANKING_LEADERBOARD, userId, totalDamage);
        }
        return worldBossRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(WORLD_BOSS_RANKING_LEADERBOARD, userId);
        if(myRank == null)
           return 0L;
        myRank = myRank + 1L;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount  = redisLongTemplate.opsForZSet().size(WORLD_BOSS_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;

        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser (Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(WORLD_BOSS_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<WorldBossRankingInfoDto> list = new ArrayList<>();
        WorldBossRankingInfoDto myRankingInfo = null;
        int ranking = 1;
        double tempTotalDamage = 0L;
        int tempRanking = 0;
        for(ZSetOperations.TypedTuple<Long> user : rankings) {
            if(ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            WorldBossRedisRanking value = worldBossRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if(tempTotalDamage != value.getTotalDamage()) {
                tempTotalDamage = value.getTotalDamage();
                tempRanking = ranking;
            }

            WorldBossRankingInfoDto worldBossRankingInfoDto = new WorldBossRankingInfoDto();
            worldBossRankingInfoDto.SetWorldBossRankingInfoDto(id, value.getUserGameName(), tempRanking, tempTotalDamage, 0);
            list.add(worldBossRankingInfoDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = worldBossRankingInfoDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null){
            myRankingInfo = new WorldBossRankingInfoDto();
            WorldBossRanking worldBossRanking = worldBossRankingRepository.findByUseridUser(userId).orElse(null);
            if (worldBossRanking != null)
                myRankingInfo.SetWorldBossRankingInfoDto(userId, worldBossRanking.getUserGameName(), getRank(userId).intValue(), worldBossRanking.getTotalDamage(), getPercent(userId));
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetWorldBossRankingInfoDto(userId, user.getUserGameName(), 0, 0L, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
