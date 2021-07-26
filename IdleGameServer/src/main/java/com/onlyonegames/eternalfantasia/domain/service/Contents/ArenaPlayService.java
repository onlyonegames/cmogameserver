package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ArenaRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyArenaPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class ArenaPlayService {
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final ErrorLoggingService errorLoggingService;
    private final ArenaRankingRepository arenaRankingRepository;
    private final ArenaLeaderboardService arenaLeaderboardService;
    private final UserRepository userRepository;

    public Map<String, Object> ArenaPlayTimeSet(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            //TODO ErrorCode add
        }
        return map;
    }

    public Map<String, Object> ArenaPlay(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            //TODO ErrorCode add
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            //TODO ErrorCode add
        }
        if(!myArenaPlayData.SpendPlayableCount()){
            if(!user.SpendDiamond(100)) {
                //TODO ErrorCode add
            }
        }
        map.put("diamond", user.getDiamond());
        myArenaPlayData.ResetResetAbleMatchingUser();
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
            //TODO ErrorCode add
        }
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

        map.put("myArenaPlayData", myArenaPlayData);
        map.put("arenaRanking", saveRanking);

        return map;
    }

    public Map<String, Object> ArenaFail(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            //TODO ErrorCode add
        }
        myArenaPlayData.ResetReMatchingAbleCount();
        int userScore = 0;
        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        if(arenaRanking != null)
            userScore = arenaRanking.getPoint();

        userScore += 3;

        userScore = MathHelper.Clamp(userScore, 0, Integer.MAX_VALUE);

        ArenaRanking saveRanking = arenaLeaderboardService.setScore(userId, userScore);
        Long changeRanking = arenaLeaderboardService.getRank(saveRanking.getUseridUser());
        saveRanking.SetRanking(changeRanking.intValue());

        map.put("myArenaPlayData", myArenaPlayData);
        map.put("arenaRanking", saveRanking);

        return map;
    }
}
