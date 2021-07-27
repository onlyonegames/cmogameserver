package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.MyWorldBossPlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.WorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyWorldBossPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class WorldBossPlayService {
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final WorldBossLeaderboardService worldBossLeaderboardService;
    private final MyWorldBossPlayDataRepository myWorldBossPlayDataRepository;
    private final UserRepository userRepository;

    public Map<String, Object> GetMyInfo(Long userId, Map<String, Object> map) {
        MyWorldBossPlayData myWorldBossPlayData = myWorldBossPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myWorldBossPlayData == null) {
            MyWorldBossPlayDataDto myWorldBossPlayDataDto = new MyWorldBossPlayDataDto();
            myWorldBossPlayDataDto.setUseridUser(userId);
            myWorldBossPlayData = myWorldBossPlayDataRepository.save(myWorldBossPlayDataDto.ToEntity());
        }
        WorldBossRanking worldBossRanking = worldBossRankingRepository.findByUseridUser(userId).orElse(null);
        if(worldBossRanking == null) {
            WorldBossRankingDto worldBossRankingDto = new WorldBossRankingDto();
            worldBossRankingDto.SetFirstUser();
            map.put("worldBossRanking", worldBossRankingDto);
        }
        else {
            worldBossRanking.ResetRanking(worldBossLeaderboardService.getRank(userId));
            map.put("worldBossRanking", worldBossRanking);
        }
        map.put("myWorldBossPlayData", myWorldBossPlayData);
        return map;
    }

    public Map<String, Object> WorldBossPlay(Long userId, Map<String, Object> map) {
        MyWorldBossPlayData myWorldBossPlayData = myWorldBossPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myWorldBossPlayData == null) {
            //TODO ErrorCode
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            //TODO ErrorCode
        }
        if(!myWorldBossPlayData.SpendPlayableCount()) {
            if(!user.SpendDiamond(100)) {
                //TODO ErrorCode
            }
        }
        map.put("diamond", user.getDiamond());
        return map;
    }

    public Map<String, Object> WorldBossFinish(Long userId, Long totalDamage, Map<String, Object> map) {
        Long damage = 0L;
        WorldBossRanking worldBossRanking = worldBossRankingRepository.findByUseridUser(userId).orElse(null);
        if(worldBossRanking != null) {
            damage = worldBossRanking.getTotalDamage();
            worldBossRanking.ResetBestDamage(totalDamage);
        }
        damage += totalDamage;

        damage = MathHelper.Clamp(damage, 0, Long.MAX_VALUE);

        WorldBossRanking saveRanking = worldBossLeaderboardService.setScore(userId, damage);
        Long changeRanking = worldBossLeaderboardService.getRank(saveRanking.getUseridUser());
        saveRanking.ResetRanking(changeRanking);

        map.put("worldBossRanking", saveRanking);
        return map;
    }
}
