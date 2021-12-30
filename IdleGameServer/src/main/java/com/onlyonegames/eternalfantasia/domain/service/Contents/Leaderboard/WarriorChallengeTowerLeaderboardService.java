package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ChallengeTowerWarriorRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerWarriorRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ChallengeTowerWarriorRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerWarriorRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ChallengeTowerWarriorRedisRankingRepository;
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
public class WarriorChallengeTowerLeaderboardService {
    public static String WARRIOR_CHALLENGE_RANKING_LEADERBOARD = "warrior_challenge_ranking_leaderboard";
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final ChallengeTowerWarriorRankingRepository challengeTowerWarriorRankingRepository;
    private final ChallengeTowerWarriorRedisRankingRepository challengeTowerWarriorRedisRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;

    public ChallengeTowerWarriorRanking setScore(Long userId, int point) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find. userId => " + userId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find. userId => " + userId, ResponseErrorCode.NOT_FIND_DATA);
        }

        ChallengeTowerWarriorRanking challengeTowerWarriorRanking = challengeTowerWarriorRankingRepository.findByUseridUser(userId).orElse(null);
        if (challengeTowerWarriorRanking == null) {
            challengeTowerWarriorRanking = ChallengeTowerWarriorRanking.builder().point(point).useridUser(userId).userGameName(user.getUserGameName()).build();
            challengeTowerWarriorRanking = challengeTowerWarriorRankingRepository.save(challengeTowerWarriorRanking);
        }
        else {
            challengeTowerWarriorRanking.refresh(point);
            challengeTowerWarriorRanking.ResetUserGameName(user.getUserGameName());
        }
        if (!user.isDummyUser()) {
            ChallengeTowerWarriorRedisRanking challengeTowerWarriorRedisRanking = challengeTowerWarriorRedisRankingRepository.findById(userId).orElse(null);
            if (challengeTowerWarriorRedisRanking == null)
                challengeTowerWarriorRedisRanking = ChallengeTowerWarriorRedisRanking.builder().id(userId).point(point).userGameName(user.getUserGameName()).build();
            else {
                challengeTowerWarriorRedisRanking.refresh(point);
                challengeTowerWarriorRedisRanking.ResetUserGameName(user.getUserGameName());
            }
            challengeTowerWarriorRedisRankingRepository.save(challengeTowerWarriorRedisRanking);

            redisLongTemplate.opsForZSet().add(WARRIOR_CHALLENGE_RANKING_LEADERBOARD, userId, point);
        }
        return challengeTowerWarriorRanking;
    }

    public Long getRank(Long userId) {
        Long myRank = redisLongTemplate.opsForZSet().reverseRank(WARRIOR_CHALLENGE_RANKING_LEADERBOARD, userId);
        if (myRank == null)
            return 0L;
        myRank = myRank + 1;
        return myRank;
    }

    public double getPercent(Long userId) {
        Long totalCount = redisLongTemplate.opsForZSet().size(WARRIOR_CHALLENGE_RANKING_LEADERBOARD);
        if (totalCount == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: Redis Score Not Find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Redis Score Not Find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (totalCount == 0L)
            return 0;
        return MathHelper.Round2(getRank(userId) * 100d / totalCount);
    }

    public Map<String, Object> GetLeaderboardForAllUser(Long userId, long bottom, long top, Map<String, Object> map) {
        Set<ZSetOperations.TypedTuple<Long>> rankings = Optional.ofNullable(redisLongTemplate.opsForZSet().reverseRangeWithScores(WARRIOR_CHALLENGE_RANKING_LEADERBOARD, bottom, top)).orElse(Collections.emptySet());
        List<ChallengeTowerWarriorRankingDto> list = new ArrayList<>();
        ChallengeTowerWarriorRankingDto myRankingInfo = null;
        int ranking = 1;
        int tempPoint = 0;
        int tempRanking = 0;
        for (ZSetOperations.TypedTuple<Long> user : rankings) {
            if (ranking > 100)
                break;
            Long id = user.getValue();
            if (id == null)
                continue;
            ChallengeTowerWarriorRedisRanking value = challengeTowerWarriorRedisRankingRepository.findById(id).orElse(null);
            if (value == null)
                continue;
            if (tempPoint != value.getPoint()) {
                tempPoint = value.getPoint();
                tempRanking = ranking;
            }

            ChallengeTowerWarriorRankingDto challengeTowerWarriorRankingDto = new ChallengeTowerWarriorRankingDto();
            challengeTowerWarriorRankingDto.SetWarriorChallengeTowerRankingDto(id, value.getUserGameName(), tempRanking, tempPoint, 0);
            list.add(challengeTowerWarriorRankingDto);
            ranking++;
            if (id.equals(userId)) {
                myRankingInfo = challengeTowerWarriorRankingDto;
                myRankingInfo.setTotalPercent(getPercent(userId));
            }
        }

        if (myRankingInfo == null) {
            myRankingInfo = new ChallengeTowerWarriorRankingDto();
            ChallengeTowerWarriorRanking challengeTowerWarriorRanking = challengeTowerWarriorRankingRepository.findByUseridUser(userId).orElse(null);
            if (challengeTowerWarriorRanking != null) {
                int myRanking = 0;
                ChallengeTowerWarriorRankingDto temp = list.stream().filter(i -> i.getPoint() == challengeTowerWarriorRanking.getPoint()).findAny().orElse(null);
                if (temp == null)
                    myRanking = getRank(userId).intValue();
                else
                    myRanking = temp.getRanking();
                myRankingInfo.SetWarriorChallengeTowerRankingDto(userId, challengeTowerWarriorRanking.getUserGameName(), myRanking, challengeTowerWarriorRanking.getRanking(), getPercent(userId));
            }
            else {
                User user = userRepository.findById(userId).orElse(null);
                if (user == null) {
                    errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                    throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
                }
                myRankingInfo.SetWarriorChallengeTowerRankingDto(userId, user.getUserGameName(), 0, 0, 0);
            }
        }
        map.put("myRankingInfo", myRankingInfo);
        map.put("ranking", list);
        return map;
    }
}
