package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.ArenaPlayLogDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.ArenaPlayLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ArenaRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyArenaPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.ArenaPlayLogRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ArenaPlayService {
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final ErrorLoggingService errorLoggingService;
    private final ArenaRankingRepository arenaRankingRepository;
    private final ArenaLeaderboardService arenaLeaderboardService;
    private final UserRepository userRepository;
    private final ArenaPlayLogRepository arenaPlayLogRepository;

    public Map<String, Object> ArenaPlayTimeSet(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaPlayData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaPlayData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        return map;
    }

    public Map<String, Object> ArenaPlay(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaPlayData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaPlayData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!myArenaPlayData.SpendPlayableCount()){
            if(!user.SpendDiamond(150)) {
                //TODO ErrorCode add
            }
        }
        map.put("diamond", user.getDiamond());
        myArenaPlayData.ResetResetAbleMatchingUser();
        ArenaPlayLogDto arenaPlayLogDto = new ArenaPlayLogDto();
        arenaPlayLogDto.SetArenaPlayLogDto(userId, myArenaPlayData.getMatchedUserId());
        ArenaPlayLog arenaPlayLog = arenaPlayLogRepository.save(arenaPlayLogDto.ToEntity());
        myArenaPlayData.SetArenaPlayLogId(arenaPlayLog.getId());
        return map;
    }

//    public Map<String, Object> ArenaForcePlay(Long userId, Map<String, Object> map) {
//        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
//        if(myArenaPlayData == null) {
//            //TODO ErrorCode add
//        }
//    }

    public Map<String, Object> ArenaWin(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaPlayData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaPlayData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.AddArenaCoin(50L);
        myArenaPlayData.ResetReMatchingAbleCount();
        int userScore = 0;
        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        if(arenaRanking != null)
            userScore = arenaRanking.getPoint();

        userScore += 100;

        userScore = MathHelper.Clamp(userScore, 0, Integer.MAX_VALUE);

        ArenaRanking saveRanking = arenaLeaderboardService.setScore(userId, userScore);
        Long changeRanking = arenaLeaderboardService.getRank(saveRanking.getUseridUser());
        saveRanking.SetRanking(changeRanking.intValue());

        ArenaPlayLog arenaPlayLog = arenaPlayLogRepository.findById(myArenaPlayData.getArenaPlayLogId()).orElse(null);
        if (arenaPlayLog == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaPlayLog not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaPlayLog not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        arenaPlayLog.SetWin();
        map.put("myArenaPlayData", myArenaPlayData);
        map.put("arenaRanking", saveRanking);
        map.put("arenaCoin", user.getArenaCoin());

        return map;
    }

    public Map<String, Object> ArenaFail(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaPlayData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaPlayData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        user.AddArenaCoin(5L);
        myArenaPlayData.ResetReMatchingAbleCount();
        int userScore = 0;
        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        if(arenaRanking != null)
            userScore = arenaRanking.getPoint();

        userScore += 10;

        userScore = MathHelper.Clamp(userScore, 0, Integer.MAX_VALUE);

        ArenaRanking saveRanking = arenaLeaderboardService.setScore(userId, userScore);
        Long changeRanking = arenaLeaderboardService.getRank(saveRanking.getUseridUser());
        saveRanking.SetRanking(changeRanking.intValue());

        ArenaPlayLog arenaPlayLog = arenaPlayLogRepository.findById(myArenaPlayData.getArenaPlayLogId()).orElse(null);
        if (arenaPlayLog == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaPlayLog not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaPlayLog not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        arenaPlayLog.SetFail();

        map.put("myArenaPlayData", myArenaPlayData);
        map.put("arenaRanking", saveRanking);
        map.put("arenaCoin", user.getArenaCoin());

        return map;
    }
}
