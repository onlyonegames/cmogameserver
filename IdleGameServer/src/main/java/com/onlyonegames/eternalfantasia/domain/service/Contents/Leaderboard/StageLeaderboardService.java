package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.StageRankingInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.StageRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.StageRedisRankingRepository;
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
public class StageLeaderboardService {
    public static String STAGE_RANKING_LEADERBOARD = "stage_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final StageRedisRankingRepository stageRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final StageRankingRepository stageRankingRepository;
    private final UserRepository userRepository;

    public StageRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        StageRanking stageRanking = stageRankingRepository.findByUseridUser(userId).orElse(null);
        if(stageRanking == null) {
            stageRanking = StageRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            stageRanking = stageRankingRepository.save(stageRanking);
        }
        else {
            stageRanking.refresh(point);
            stageRanking.ResetUserGameName(user.getUserGameName());
        }
        if(!user.isDummyUser()) {
            StageRedisRanking stageRedisRanking = stageRedisRankingRepository.findById(userId).orElse(null);
            if(stageRedisRanking == null)
                stageRedisRanking = StageRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                stageRedisRanking.refresh(point);
                stageRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            stageRedisRankingRepository.save(stageRedisRanking);

            redisLongTemplate.opsForZSet().add(STAGE_RANKING_LEADERBOARD, userId, point);
        }
        return stageRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(STAGE_RANKING_LEADERBOARD, userId);
        if(myRank == null)
            return 0L;
        myRank = myRank + 1L;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount  = redisLongTemplate.opsForZSet().size(STAGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser (Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(STAGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<StageRankingInfoDto> list = new ArrayList<>();
        StageRankingInfoDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for(ZSetOperations.TypedTuple<Long> user : rankings) {
            if(ranking > 100)
                break;
            Long id = user.getValue();
            StageRedisRanking value = stageRedisRankingRepository.findById(id).get();
            if(tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            StageRankingInfoDto stageRankingInfoDto = new StageRankingInfoDto();
            stageRankingInfoDto.SetStageRankingInfoDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(stageRankingInfoDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = stageRankingInfoDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null){
            myRankingInfo = new StageRankingInfoDto();
            StageRanking stageRanking = stageRankingRepository.findByUseridUser(userId).orElse(null);
            if (stageRanking != null)
                myRankingInfo.SetStageRankingInfoDto(userId, stageRanking.getUserGameName(), getRank(userId).intValue(), stageRanking.getPoint(), getPercent(userId));
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetStageRankingInfoDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
