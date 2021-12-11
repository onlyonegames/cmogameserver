package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.MyArenaPlayDataDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ArenaMatchingService {
    private final UserRepository userRepository;
    private final ArenaRankingRepository arenaRankingRepository;
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final ArenaLeaderboardService arenaLeaderboardService;
    private final ErrorLoggingService errorLoggingService;

    private ArenaRanking GetReadyVersus(Long userId){
        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        int low;
        int high;
        if(arenaRanking == null || arenaRanking.getRanking() == 0) {
            long baseRank = arenaRankingRepository.count();
            low = (int) baseRank;
            high = baseRank <= 30 ? 0 :(int) baseRank - 30;
        }
        else{
            low = arenaRanking.getRanking() + 30;
            high = arenaRanking.getRanking() <= 30 ? 0: arenaRanking.getRanking()- 30; //TODO 조건이 if 문으로 변경될 가능성 있음
        }
        //List<ArenaRanking> probabilityList = arenaRankingRepository.findAllByRankingGreaterThanAndRankingLessThan(high, low);
        List<Long> userIdList = new ArrayList<>(arenaLeaderboardService.getRangeOfMatch(userId));

        List<ArenaRanking> probabilityList = arenaRankingRepository.findAllByUseridUserIn(userIdList);
        ArenaRanking mine = null;
        if(arenaRanking != null)
            mine = probabilityList.stream().filter(i -> i.getId().equals(arenaRanking.getId())).findAny().orElse(null);
        if(mine != null)
            probabilityList.remove(mine);
        int listSize = probabilityList.size();
        if(listSize <= 0) {
            //TODO ErrorLogging Add
        }
        int selectedIndex = (int) MathHelper.Range(0, listSize-1);
        ArenaRanking selectedUser = probabilityList.get(selectedIndex);
        return selectedUser;
    }

    public Map<String, Object> GetReadyVersus(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            MyArenaPlayDataDto myArenaPlayDataDto = new MyArenaPlayDataDto();
            myArenaPlayDataDto.setUseridUser(userId);
            myArenaPlayData = myArenaPlayDataRepository.save(myArenaPlayDataDto.ToEntity());
        }
        if(myArenaPlayData.getMatchedUserId().equals(0L) || myArenaPlayData.isResetAbleMatchingUser()) {
            ArenaRanking versus = GetReadyVersus(userId);
            myArenaPlayData.SetMatchedUserId(versus.getUseridUser());
            myArenaPlayData.ResetUnResetAbleMatchingUser();
        }

        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        if(arenaRanking == null) {
            ArenaRankingDto arenaRankingDto = new ArenaRankingDto();
            arenaRankingDto.SetFirstUser();
            map.put("arenaRanking", arenaRankingDto);
        }
        else {
            arenaRanking.SetRanking(arenaLeaderboardService.getRank(userId).intValue());
            map.put("arenaRanking", arenaRanking);
        }

        ArenaRanking enemyArenaRanking = arenaRankingRepository.findByUseridUser(myArenaPlayData.getMatchedUserId()).orElse(null);
        if(enemyArenaRanking == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        enemyArenaRanking.SetRanking(arenaLeaderboardService.getRank(myArenaPlayData.getMatchedUserId()).intValue());
        map.put("enemyArenaRanking", enemyArenaRanking);

        User enemyUser = userRepository.findById(myArenaPlayData.getMatchedUserId()).orElse(null);
        if(enemyUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: enemyUser not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: enemyUser not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("enemyUserBattleStatus", enemyUser.getBattleStatus());
        map.put("myArenaPlayData", myArenaPlayData);
        return map;
    }

    public Map<String, Object> ForceGetReadyVersus(Long userId, Map<String, Object> map) {
        MyArenaPlayData myArenaPlayData = myArenaPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyArenaPlayData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaPlayData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!myArenaPlayData.SpendReMatchingAbleCount()) {
            //TODO ErrorLogging Add
        }

        ArenaRanking versus = GetReadyVersus(userId);
        myArenaPlayData.SetMatchedUserId(versus.getUseridUser());

        ArenaRanking arenaRanking = arenaRankingRepository.findByUseridUser(userId).orElse(null);
        if(arenaRanking == null) {
            ArenaRankingDto arenaRankingDto = new ArenaRankingDto();
            arenaRankingDto.SetFirstUser();
            map.put("arenaRanking", arenaRankingDto);
        }
        else
            map.put("arenaRanking", arenaRanking);

        ArenaRanking enemyArenaRanking = arenaRankingRepository.findByUseridUser(myArenaPlayData.getMatchedUserId()).orElse(null);
        if(enemyArenaRanking == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EnemyArenaRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EnemyArenaRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("enemyArenaRanking", enemyArenaRanking);

        User enemyUser = userRepository.findById(myArenaPlayData.getMatchedUserId()).orElse(null);
        if(enemyUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: EnemyUser not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: EnemyUser not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("enemyUserBattleStatus", enemyUser.getBattleStatus());
        map.put("myArenaPlayData", myArenaPlayData);
        return map;
    }
}
