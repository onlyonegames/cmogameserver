package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ChallengeTowerThiefRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerThiefRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerThiefRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerThiefRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerThiefRedisRankingRepository;
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
public class ThiefChallengeTowerLeaderboardService {
    public static String THIEF_CHALLENGE_RANKING_LEADERBOARD = "thief_challenge_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final ChallengeTowerThiefRankingRepository challengeTowerThiefRankingRepository;
    private final ChallengeTowerThiefRedisRankingRepository challengeTowerThiefRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;

    public ChallengeTowerThiefRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ChallengeTowerThiefRanking challengeTowerThiefRanking = challengeTowerThiefRankingRepository.findByUseridUser(userId).orElse(null);
        if (challengeTowerThiefRanking == null) {
            challengeTowerThiefRanking = ChallengeTowerThiefRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            challengeTowerThiefRanking = challengeTowerThiefRankingRepository.save(challengeTowerThiefRanking);
        }
        else {
            challengeTowerThiefRanking.refresh(point);
            challengeTowerThiefRanking.ResetUserGameName(user.getUserGameName());
        }
        if (!user.isDummyUser()) {
            ChallengeTowerThiefRedisRanking challengeTowerThiefRedisRanking = challengeTowerThiefRedisRankingRepository.findById(userId).orElse(null);
            if (challengeTowerThiefRedisRanking == null)
                challengeTowerThiefRedisRanking = ChallengeTowerThiefRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                challengeTowerThiefRedisRanking.refresh(point);
                challengeTowerThiefRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            challengeTowerThiefRedisRankingRepository.save(challengeTowerThiefRedisRanking);

            redisLongTemplate.opsForZSet().add(THIEF_CHALLENGE_RANKING_LEADERBOARD, userId, point);
        }
        return challengeTowerThiefRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(THIEF_CHALLENGE_RANKING_LEADERBOARD, userId);
        if (myRank == null)
            return 0L;
        myRank = myRank + 1;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount = redisLongTemplate.opsForZSet().size(THIEF_CHALLENGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(THIEF_CHALLENGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<ChallengeTowerThiefRankingDto> list = new ArrayList<>();
        ChallengeTowerThiefRankingDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if (ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            ChallengeTowerThiefRedisRanking value = challengeTowerThiefRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            ChallengeTowerThiefRankingDto challengeTowerThiefRankingDto = new ChallengeTowerThiefRankingDto();
            challengeTowerThiefRankingDto.SetThiefChallengeTowerRankingDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(challengeTowerThiefRankingDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = challengeTowerThiefRankingDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null) {
            myRankingInfo = new ChallengeTowerThiefRankingDto();
            ChallengeTowerThiefRanking challengeTowerThiefRanking = challengeTowerThiefRankingRepository.findByUseridUser(userId).orElse(null);
            if (challengeTowerThiefRanking != null) {
                int myRanking = 0;
                ChallengeTowerThiefRankingDto temp = list.stream().filter(i -> i.getPoint() == challengeTowerThiefRanking.getPoint()).findAny().orElse(null);
                if (temp == null)
                    myRanking = getRank(userId).intValue();
                else
                    myRanking = temp.getRanking();
                myRankingInfo.SetThiefChallengeTowerRankingDto(userId, challengeTowerThiefRanking.getUserGameName(), myRanking, challengeTowerThiefRanking.getRanking(), getPercent(userId));
            }
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetThiefChallengeTowerRankingDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
