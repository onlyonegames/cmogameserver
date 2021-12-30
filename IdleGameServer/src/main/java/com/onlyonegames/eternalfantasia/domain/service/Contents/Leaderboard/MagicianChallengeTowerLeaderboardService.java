package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ChallengeTowerMagicianRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerMagicianRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerMagicianRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerMagicianRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerMagicianRedisRankingRepository;
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
public class MagicianChallengeTowerLeaderboardService {
    public static String MAGICIAN_CHALLENGE_RANKING_LEADERBOARD = "magician_challenge_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final ChallengeTowerMagicianRankingRepository challengeTowerMagicianRankingRepository;
    private final ChallengeTowerMagicianRedisRankingRepository challengeTowerMagicianRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;

    public ChallengeTowerMagicianRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ChallengeTowerMagicianRanking challengeTowerMagicianRanking = challengeTowerMagicianRankingRepository.findByUseridUser(userId).orElse(null);
        if (challengeTowerMagicianRanking == null) {
            challengeTowerMagicianRanking = ChallengeTowerMagicianRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            challengeTowerMagicianRanking = challengeTowerMagicianRankingRepository.save(challengeTowerMagicianRanking);
        }
        else {
            challengeTowerMagicianRanking.refresh(point);
            challengeTowerMagicianRanking.ResetUserGameName(user.getUserGameName());
        }
        if (!user.isDummyUser()) {
            ChallengeTowerMagicianRedisRanking challengeTowerMagicianRedisRanking = challengeTowerMagicianRedisRankingRepository.findById(userId).orElse(null);
            if (challengeTowerMagicianRedisRanking == null)
                challengeTowerMagicianRedisRanking = ChallengeTowerMagicianRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                challengeTowerMagicianRedisRanking.refresh(point);
                challengeTowerMagicianRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            challengeTowerMagicianRedisRankingRepository.save(challengeTowerMagicianRedisRanking);

            redisLongTemplate.opsForZSet().add(MAGICIAN_CHALLENGE_RANKING_LEADERBOARD, userId, point);
        }
        return challengeTowerMagicianRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(MAGICIAN_CHALLENGE_RANKING_LEADERBOARD, userId);
        if (myRank == null)
            return 0L;
        myRank = myRank + 1;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount = redisLongTemplate.opsForZSet().size(MAGICIAN_CHALLENGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(MAGICIAN_CHALLENGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<ChallengeTowerMagicianRankingDto> list = new ArrayList<>();
        ChallengeTowerMagicianRankingDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if (ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            ChallengeTowerMagicianRedisRanking value = challengeTowerMagicianRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            ChallengeTowerMagicianRankingDto challengeTowerMagicianRankingDto = new ChallengeTowerMagicianRankingDto();
            challengeTowerMagicianRankingDto.SetMagicianChallengeTowerRankingDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(challengeTowerMagicianRankingDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = challengeTowerMagicianRankingDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null) {
            myRankingInfo = new ChallengeTowerMagicianRankingDto();
            ChallengeTowerMagicianRanking challengeTowerMagicianRanking = challengeTowerMagicianRankingRepository.findByUseridUser(userId).orElse(null);
            if (challengeTowerMagicianRanking != null) {
                int myRanking = 0;
                ChallengeTowerMagicianRankingDto temp = list.stream().filter(i -> i.getPoint() == challengeTowerMagicianRanking.getPoint()).findAny().orElse(null);
                if (temp == null)
                    myRanking = getRank(userId).intValue();
                else
                    myRanking = temp.getRanking();
                myRankingInfo.SetMagicianChallengeTowerRankingDto(userId, challengeTowerMagicianRanking.getUserGameName(), myRanking, challengeTowerMagicianRanking.getRanking(), getPercent(userId));
            }
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetMagicianChallengeTowerRankingDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
