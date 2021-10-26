package com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousBattlePowerRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousStageRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousWorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousBattlePowerRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousStageRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousWorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousArenaRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousBattlePowerRankingRepository;
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
    private final PreviousBattlePowerRankingRepository previousBattlePowerRankingRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetPreviousArenaRanking(Long userId, Map<String, Object> map){
        List<PreviousArenaRanking> previousArenaRankingList = previousArenaRankingRepository.findAll(Sort.by(Sort.Direction.ASC, "ranking"));
        List<PreviousArenaRanking> tempList = new ArrayList<>();
        for (PreviousArenaRanking temp : previousArenaRankingList) {
            if (temp.getRanking() > 100)
                continue;
            tempList.add(temp);
        }
        long totalCount = previousArenaRankingRepository.count();
        PreviousArenaRanking myPreviousArenaRanking = previousArenaRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousArenaRanking == null) {
            PreviousArenaRankingDto previousArenaRankingDto = new PreviousArenaRankingDto();
            previousArenaRankingDto.setPoint(0);
            previousArenaRankingDto.setRanking(0);
            map.put("myPreviousArenaRanking", previousArenaRankingDto);
            map.put("myPercent", 0d);
        }
        else {
            map.put("myPreviousArenaRanking", myPreviousArenaRanking);
            double myPercent = MathHelper.Round2(myPreviousArenaRanking.getRanking() * 100d / totalCount);
            map.put("myPercent", myPercent);
        }
        map.put("previousArenaRanking", previousArenaRankingList);
        return map;
    }

    public Map<String, Object> GetPreviousWorldBossRanking(Long userId, Map<String, Object> map){
//        List<PreviousWorldBossRanking> previousWorldBossRankingList = previousWorldBossRankingRepository.findAll(Sort.by(Sort.Direction.ASC, "ranking"));
//        List<PreviousWorldBossRanking> tempList = new ArrayList<>();
//        for(PreviousWorldBossRanking temp : previousWorldBossRankingList) {
//            if(temp.getRanking() > 100)
//                continue;
//            tempList.add(temp);
//        }
        long totalCount = previousWorldBossRankingRepository.count();
        PreviousWorldBossRanking myPreviousWorldBossRanking = previousWorldBossRankingRepository.findByUseridUser(userId).orElse(null);//previousWorldBossRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousWorldBossRanking == null) {
            PreviousWorldBossRankingDto previousWorldBossRankingDto = new PreviousWorldBossRankingDto();
            previousWorldBossRankingDto.setTotalDamage(0d);
            previousWorldBossRankingDto.setRanking(0);
            map.put("myPreviousWorldBossRanking", previousWorldBossRankingDto);
            map.put("myPercent", 0d);
        }
        else {
            map.put("myPreviousWorldBossRanking", myPreviousWorldBossRanking);
            double myPercent = MathHelper.Round2(myPreviousWorldBossRanking.getRanking() * 100d / totalCount);
            map.put("myPercent", myPercent);
        }

//        map.put("previousWorldBossRanking", tempList);
        return map;
    }

    public Map<String, Object> GetPreviousStageRanking(Long userId, Map<String, Object> map){
        List<PreviousStageRanking> previousStageRankingList = previousStageRankingRepository.findAll(Sort.by(Sort.Direction.ASC, "ranking"));
        List<PreviousStageRanking> tempList = new ArrayList<>();
        for (PreviousStageRanking temp : previousStageRankingList) {
            if(temp.getRanking() > 100)
                continue;
            tempList.add(temp);
        }
        Long totalCount = previousStageRankingRepository.count();
        PreviousStageRanking myPreviousStageRanking = previousStageRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousStageRanking == null) {
            PreviousStageRankingDto previousStageRankingDto = new PreviousStageRankingDto();
            previousStageRankingDto.setPoint(0);
            previousStageRankingDto.setRanking(0);
            map.put("myPreviousStageRanking", previousStageRankingDto);
            map.put("myPercent", 0d);
        }
        else {
            map.put("myPreviousStageRanking", myPreviousStageRanking);
            double myPercent = MathHelper.Round2(myPreviousStageRanking.getRanking() * 100d / totalCount);
            map.put("myPercent", myPercent);
        }
        map.put("previousStageRanking", tempList);
        return map;
    }

    public Map<String, Object> GetPreviousBattleRanking(Long userId, Map<String, Object> map) {
        List<PreviousBattlePowerRanking> previousBattlePowerRankingList = previousBattlePowerRankingRepository.findAll(Sort.by(Sort.Direction.ASC, "ranking"));
        List<PreviousBattlePowerRanking> tempList = new ArrayList<>();
        for (PreviousBattlePowerRanking temp : previousBattlePowerRankingList) {
            if (temp.getRanking() > 100)
                continue;
            tempList.add(temp);
        }
        Long totalCount = previousBattlePowerRankingRepository.count();
        PreviousBattlePowerRanking myPreviousBattlePowerRanking = previousBattlePowerRankingList.stream().filter(i -> i.getUseridUser().equals(userId)).findAny().orElse(null);
        if(myPreviousBattlePowerRanking == null) {
            PreviousBattlePowerRankingDto previousBattlePowerRankingDto = new PreviousBattlePowerRankingDto();
            previousBattlePowerRankingDto.setBattlePower(0L);
            previousBattlePowerRankingDto.setRanking(0);
            map.put("myPreviousBattlePowerRanking", previousBattlePowerRankingDto);
            map.put("myPercent", 0d);
        }
        else {
            map.put("myPreviousBattlePowerRanking", myPreviousBattlePowerRanking);
            double myPercent = MathHelper.Round2(myPreviousBattlePowerRanking.getRanking() * 100d / totalCount);
            map.put("myPercent", myPercent);
        }
        map.put("previousBattlePowerRanking", tempList);
        return map;
    }
}
