package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ChallengeTowerKnightRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerKnightRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerKnightRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerKnightRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerKnightRedisRankingRepository;
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
public class KnightChallengeTowerLeaderboardService {
    public static String KNIGHT_CHALLENGE_RANKING_LEADERBOARD = "knight_challenge_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final ChallengeTowerKnightRankingRepository challengeTowerKnightRankingRepository;
    private final ChallengeTowerKnightRedisRankingRepository challengeTowerKnightRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;

    public ChallengeTowerKnightRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ChallengeTowerKnightRanking challengeTowerKnightRanking = challengeTowerKnightRankingRepository.findByUseridUser(userId).orElse(null);
        if (challengeTowerKnightRanking == null) {
            challengeTowerKnightRanking = ChallengeTowerKnightRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            challengeTowerKnightRanking = challengeTowerKnightRankingRepository.save(challengeTowerKnightRanking);
        }
        else {
            challengeTowerKnightRanking.refresh(point);
            challengeTowerKnightRanking.ResetUserGameName(user.getUserGameName());
        }
        if (!user.isDummyUser()) {
            ChallengeTowerKnightRedisRanking challengeTowerKnightRedisRanking = challengeTowerKnightRedisRankingRepository.findById(userId).orElse(null);
            if (challengeTowerKnightRedisRanking == null)
                challengeTowerKnightRedisRanking = ChallengeTowerKnightRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                challengeTowerKnightRedisRanking.refresh(point);
                challengeTowerKnightRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            challengeTowerKnightRedisRankingRepository.save(challengeTowerKnightRedisRanking);

            redisLongTemplate.opsForZSet().add(KNIGHT_CHALLENGE_RANKING_LEADERBOARD, userId, point);
        }
        return challengeTowerKnightRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(KNIGHT_CHALLENGE_RANKING_LEADERBOARD, userId);
        if (myRank == null)
            return 0L;
        myRank = myRank + 1;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount = redisLongTemplate.opsForZSet().size(KNIGHT_CHALLENGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(KNIGHT_CHALLENGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<ChallengeTowerKnightRankingDto> list = new ArrayList<>();
        ChallengeTowerKnightRankingDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if (ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            ChallengeTowerKnightRedisRanking value = challengeTowerKnightRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            ChallengeTowerKnightRankingDto challengeTowerKnightRankingDto = new ChallengeTowerKnightRankingDto();
            challengeTowerKnightRankingDto.SetKnightChallengeTowerRankingDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(challengeTowerKnightRankingDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = challengeTowerKnightRankingDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null) {
            myRankingInfo = new ChallengeTowerKnightRankingDto();
            ChallengeTowerKnightRanking challengeTowerKnightRanking = challengeTowerKnightRankingRepository.findByUseridUser(userId).orElse(null);
            if (challengeTowerKnightRanking != null) {
                int myRanking = 0;
                ChallengeTowerKnightRankingDto temp = list.stream().filter(i -> i.getPoint() == challengeTowerKnightRanking.getPoint()).findAny().orElse(null);
                if (temp == null)
                    myRanking = getRank(userId).intValue();
                else
                    myRanking = temp.getRanking();
                myRankingInfo.SetKnightChallengeTowerRankingDto(userId, challengeTowerKnightRanking.getUserGameName(), myRanking, challengeTowerKnightRanking.getRanking(), getPercent(userId));
            }
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetKnightChallengeTowerRankingDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
