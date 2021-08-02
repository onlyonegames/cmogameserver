package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousArenaRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.PreviousWorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.StandardTime;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.*;
import com.onlyonegames.eternalfantasia.domain.repository.MyContentsInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.StandardTimeRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.ArenaLeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class StandardTimeResetService {
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final StandardTimeRepository standardTimeRepository;
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final ArenaRankingRepository arenaRankingRepository;
    private final PreviousArenaRankingRepository previousArenaRankingRepository;
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final PreviousWorldBossRankingRepository previousWorldBossRankingRepository;
    private final ArenaLeaderboardService arenaLeaderboardService;
    private final WorldBossLeaderboardService worldBossLeaderboardService;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final MyWorldBossPlayDataRepository myWorldBossPlayDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> CheckTime(Map<String, Object> map) {
        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        if(standardTime == null) {
           //TODO ErrorCode add
        }

        LocalDateTime now = LocalDateTime.now();

        if(standardTime.getBaseDayTime().isBefore(now)){
            //TODO 일별 컨텐츠 업데이트 코드 또는 서비스 추가
            ResetWorldBossRanking();
            ResetArenaForDay();
            standardTime.SetBaseDayTime();
        }
        if(standardTime.getBaseWeekTime().isBefore(now)){
            //TODO 주별 컨텐츠 업데이트 코드 또는 서비스 추가
            SetPreviousArenaRanking();
            ResetChallengeTower(standardTime);
            standardTime.SetBaseWeekTime();
        }
        if(standardTime.getBaseMonthTime().isBefore(now)){
            //TODO 월별 컨텐츠 업데이트 코드 또는 서비스 추가
            standardTime.SetBaseMonthTime();
        }
        return map;
    }

    private void ResetChallengeTower(StandardTime standardTime) {
        int classIndex = standardTime.getChallengeTowerClassIndex() + 1;
        standardTime.SetChallengeTowerClassIndex(classIndex<5?classIndex:0);
        List<MyContentsInfo> myContentsInfoList = myContentsInfoRepository.findAllByChallengeTowerFloorNot(0);
        for(MyContentsInfo temp : myContentsInfoList) {
            temp.SetChallengeTowerFloor(0);
        }
    }

    private void ResetArenaForDay() {
        List<MyArenaPlayData> myArenaPlayDataList = myArenaPlayDataRepository.findAllByResetAbleMatchingUserOrReMatchingAbleCountNot(false, 3);
        for(MyArenaPlayData temp : myArenaPlayDataList) {
            temp.ResetReMatchingAbleCount();
            temp.ResetPlayableCount();
        }
    }

    private void ResetWorldBossRanking() {
        List<WorldBossRanking> worldBossRankingList = worldBossRankingRepository.findAll();
        previousWorldBossRankingRepository.deleteAll();
        for(WorldBossRanking temp : worldBossRankingList) {
            PreviousWorldBossRankingDto previousWorldBossRankingDto = new PreviousWorldBossRankingDto();
            previousWorldBossRankingDto.InitFromPreviousDB(temp);
            Long ranking = worldBossLeaderboardService.getRank(temp.getUseridUser());
            previousWorldBossRankingDto.setRanking(ranking.intValue());
            previousWorldBossRankingRepository.save(previousWorldBossRankingDto.ToEntity());
            temp.ResetZero();
        }
        redisLongTemplate.opsForZSet().getOperations().delete(WorldBossLeaderboardService.WORLD_BOSS_RANKING_LEADERBOARD);
        List<MyWorldBossPlayData> myWorldBossPlayDataList = myWorldBossPlayDataRepository.findAll();
        for(MyWorldBossPlayData temp : myWorldBossPlayDataList) {
            temp.ResetPlayableCount();
        }
    }

    private void SetPreviousArenaRanking() {
        List<ArenaRanking> arenaRankingList = arenaRankingRepository.findAll();
        previousArenaRankingRepository.deleteAll();
        for(ArenaRanking temp : arenaRankingList) {
            PreviousArenaRankingDto previousArenaRankingDto = new PreviousArenaRankingDto();
            previousArenaRankingDto.InitFromPreviousDb(temp);
            Long ranking = arenaLeaderboardService.getRank(temp.getUseridUser());
            previousArenaRankingDto.setRanking(ranking.intValue());
            previousArenaRankingRepository.save(previousArenaRankingDto.ToEntity());
        }
    }
}
