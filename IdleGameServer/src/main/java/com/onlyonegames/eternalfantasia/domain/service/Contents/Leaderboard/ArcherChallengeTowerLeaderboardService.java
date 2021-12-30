package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ChallengeTowerArcherRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerArcherRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerArcherRedisRanking;;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerArcherRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerArcherRedisRankingRepository;
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
public class ArcherChallengeTowerLeaderboardService {
    public static String ARCHER_CHALLENGE_RANKING_LEADERBOARD = "archer_challenge_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final ChallengeTowerArcherRankingRepository challengeTowerArcherRankingRepository;
    private final ChallengeTowerArcherRedisRankingRepository challengeTowerArcherRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;

    public ChallengeTowerArcherRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ChallengeTowerArcherRanking challengeTowerArcherRanking = challengeTowerArcherRankingRepository.findByUseridUser(userId).orElse(null);
        if (challengeTowerArcherRanking == null) {
            challengeTowerArcherRanking = ChallengeTowerArcherRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            challengeTowerArcherRanking = challengeTowerArcherRankingRepository.save(challengeTowerArcherRanking);
        }
        else {
            challengeTowerArcherRanking.refresh(point);
            challengeTowerArcherRanking.ResetUserGameName(user.getUserGameName());
        }
        if (!user.isDummyUser()) {
            ChallengeTowerArcherRedisRanking challengeTowerArcherRedisRanking = challengeTowerArcherRedisRankingRepository.findById(userId).orElse(null);
            if (challengeTowerArcherRedisRanking == null)
                challengeTowerArcherRedisRanking = ChallengeTowerArcherRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                challengeTowerArcherRedisRanking.refresh(point);
                challengeTowerArcherRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            challengeTowerArcherRedisRankingRepository.save(challengeTowerArcherRedisRanking);

            redisLongTemplate.opsForZSet().add(ARCHER_CHALLENGE_RANKING_LEADERBOARD, userId, point);
        }
        return challengeTowerArcherRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(ARCHER_CHALLENGE_RANKING_LEADERBOARD, userId);
        if (myRank == null)
            return 0L;
        myRank = myRank + 1;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount = redisLongTemplate.opsForZSet().size(ARCHER_CHALLENGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(ARCHER_CHALLENGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<ChallengeTowerArcherRankingDto> list = new ArrayList<>();
        ChallengeTowerArcherRankingDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if (ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            ChallengeTowerArcherRedisRanking value = challengeTowerArcherRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            ChallengeTowerArcherRankingDto challengeTowerArcherRankingDto = new ChallengeTowerArcherRankingDto();
            challengeTowerArcherRankingDto.SetArcherChallengeTowerRankingDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(challengeTowerArcherRankingDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = challengeTowerArcherRankingDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null) {
            myRankingInfo = new ChallengeTowerArcherRankingDto();
            ChallengeTowerArcherRanking challengeTowerArcherRanking = challengeTowerArcherRankingRepository.findByUseridUser(userId).orElse(null);
            if (challengeTowerArcherRanking != null) {
                int myRanking = 0;
                ChallengeTowerArcherRankingDto temp = list.stream().filter(i -> i.getPoint() == challengeTowerArcherRanking.getPoint()).findAny().orElse(null);
                if (temp == null)
                    myRanking = getRank(userId).intValue();
                else
                    myRanking = temp.getRanking();
                myRankingInfo.SetArcherChallengeTowerRankingDto(userId, challengeTowerArcherRanking.getUserGameName(), myRanking, challengeTowerArcherRanking.getRanking(), getPercent(userId));
            }
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetArcherChallengeTowerRankingDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
