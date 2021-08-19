package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousStageRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousWorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousStageRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousWorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousArenaRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousStageRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousWorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PreviousRankingService {
    private final PreviousArenaRankingRepository previousArenaRankingRepository;
    private final PreviousWorldBossRankingRepository previousWorldBossRankingRepository;
    private final PreviousStageRankingRepository previousStageRankingRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetPreviousArenaRanking(Long userId, Map<String, Object> map){
        List<PreviousArenaRanking> previousArenaRankingList = previousArenaRankingRepository.findAll(Sort.by(Sort.Direction.DESC, "ranking"));
        PreviousArenaRanking myPreviousArenaRanking = previousArenaRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousArenaRanking == null) {
            PreviousArenaRankingDto previousArenaRankingDto = new PreviousArenaRankingDto();
            previousArenaRankingDto.setPoint(0);
            previousArenaRankingDto.setRanking(0);
            map.put("myPreviousArenaRanking", previousArenaRankingDto);
        }
        else
            map.put("myPreviousArenaRanking", myPreviousArenaRanking);
        map.put("previousArenaRanking", previousArenaRankingList);
        return map;
    }

    public Map<String, Object> GetPreviousWorldBossRanking(Long userId, Map<String, Object> map){
        List<PreviousWorldBossRanking> previousWorldBossRankingList = previousWorldBossRankingRepository.findAll(Sort.by(Sort.Direction.DESC, "ranking"));
        List<PreviousWorldBossRanking> tempList = new ArrayList<>();
        for(PreviousWorldBossRanking temp : previousWorldBossRankingList) {
            if(temp.getRanking() > 100)
                continue;
            tempList.add(temp);
        }
        long totalCount = previousWorldBossRankingRepository.count();
        PreviousWorldBossRanking myPreviousWorldBossRanking = previousWorldBossRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousWorldBossRanking == null) {
            PreviousWorldBossRankingDto previousWorldBossRankingDto = new PreviousWorldBossRankingDto();
            previousWorldBossRankingDto.setTotalDamage(0L);
            previousWorldBossRankingDto.setRanking(0);
            map.put("myPreviousWorldBossRanking", previousWorldBossRankingDto);
            map.put("myPercent", 0d);
        }
        else {
            map.put("myPreviousWorldBossRanking", myPreviousWorldBossRanking);
            double myPercent = MathHelper.Round2(myPreviousWorldBossRanking.getRanking() * 100d / totalCount);
            map.put("myPercent", myPercent);
        }

        map.put("previousWorldBossRanking", tempList);
        return map;
    }

    public Map<String, Object> GetPreviousStageRanking(Long userId, Map<String, Object> map){
        List<PreviousStageRanking> previousStageRankingList = previousStageRankingRepository.findAll(Sort.by(Sort.Direction.DESC, "ranking"));
        PreviousStageRanking myPreviousStageRanking = previousStageRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousStageRanking == null) {
            PreviousStageRankingDto previousStageRankingDto = new PreviousStageRankingDto();
            previousStageRankingDto.setStageNo(0);
            previousStageRankingDto.setRanking(0);
            map.put("myPreviousStageRanking", previousStageRankingDto);
        }
        else
            map.put("myPreviousStageRanking", myPreviousStageRanking);
        map.put("previousStageRanking", previousStageRankingList);
        return map;
    }
}
