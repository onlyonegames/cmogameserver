package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRedisRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Set;

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

    public WorldBossRanking setScore(Long userId, Long totalDamage) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        WorldBossRanking worldBossRanking = worldBossRankingRepository.findByUseridUser(userId).orElse(null);
        if(worldBossRanking == null) {
            worldBossRanking = WorldBossRanking.builder().useridUser(userId).userGameName(user.getUserGameName()).totalDamage(totalDamage).build();
            worldBossRanking = worldBossRankingRepository.save(worldBossRanking);
        }
        else {
            worldBossRanking.refresh(totalDamage);
        }
        if(!user.isDummyUser()) {
            WorldBossRedisRanking worldBossRedisRanking = worldBossRedisRankingRepository.findById(userId).orElse(null);
            if(worldBossRedisRanking == null)
                worldBossRedisRanking = WorldBossRedisRanking.builder().id(userId).totalDamage(totalDamage).userGameName(user.getUserGameName()).build();
            else
                worldBossRedisRanking.refresh(totalDamage);
            worldBossRedisRankingRepository.save(worldBossRedisRanking);

            redisLongTemplate.opsForZSet().add(WORLD_BOSS_RANKING_LEADERBOARD, userId, totalDamage);
        }
        return worldBossRanking;
    }

    public Long getRank(WorldBossRanking worldBossRanking) {
        Long userId = worldBossRanking.getUseridUser();
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(WORLD_BOSS_RANKING_LEADERBOARD, userId);
        if(myRank == null)
            myRank = 0L;
        myRank = myRank + 1L;
        return myRank;
    }

//    public Set<Long> get
}
