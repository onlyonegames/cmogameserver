package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.ArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.MyArenaPlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.ArenaRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyArenaPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class ArenaMatchingService {
    private final UserRepository userRepository;
    private final ArenaRankingRepository arenaRankingRepository;
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;

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
            high = arenaRanking.getRanking() <= 30 ? 0: arenaRanking.getRanking()- 30; //TODO 조건이 if문으로 변경될 가능성 있음
        }
        List<ArenaRanking> probabilityList = arenaRankingRepository.findAllByRankingGreaterThanAndRankingLessThan(high, low);
        ArenaRanking mine = null;
        if(arenaRanking != null)
            mine = probabilityList.stream().filter(i -> i.getId().equals(arenaRanking.getId())).findAny().orElse(null);
        if(mine != null)
            probabilityList.remove(mine);
        int listSize = probabilityList.size();
        if(listSize <= 0) {
            //TODO ErrorLogging Add
        }
        int selectedIndex = (int) MathHelper.Range(0, listSize);
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
        }

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
            //TODO ErrorCode add
        }
        map.put("enemyArenaRanking", enemyArenaRanking);

        User enemyUser = userRepository.findById(myArenaPlayData.getMatchedUserId()).orElse(null);
        if(enemyUser == null) {
            //TODO ErrorLogging Add
        }
        map.put("enemyUserBattleStatus", enemyUser.getBattleStatus());
        map.put("myArenaPlayData", myArenaPlayData);
        return map;
    }
}
