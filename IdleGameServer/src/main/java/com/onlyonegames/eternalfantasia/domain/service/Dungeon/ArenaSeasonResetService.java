package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.ArenaSeasonInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.PreviousArenaSeasonRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.HallofHonorDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.ArenaSeasonInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.ArenaSeasonResetData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.HallofHonor;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousArenaSeasonRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RedisScore;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonResetDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.HallofHonorRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.PreviousArenaSeasonRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RedisScoreRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.Leaderboard.LeaderboardService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.util.StringMaker;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class  ArenaSeasonResetService {
    private final RdsScoreRepository rdsScoreRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final RedisTemplate<String, Long> redisLongTemplate;
    private final RedisScoreRepository redisScoreRepository;
    private final HallofHonorRepository hallofHonorRepository;
    private final PreviousArenaSeasonRankingRepository previousArenaSeasonRankingRepository;
    private final GameDataTableService gameDataTableService;
    private final ArenaSeasonResetDataRepository arenaSeasonResetDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> SeasonCheck(Map<String, Object> map) {
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int previousSeasonId = (int)seasonInfosCount;
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo(previousSeasonId)
                .orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        LocalDateTime startTime = arenaSeasonInfoData.getSeasonStartTime();
        LocalDateTime endTime = arenaSeasonInfoData.getSeasonEndTime();
        LocalDateTime now = LocalDateTime.now();
        if(startTime.isBefore(now) && endTime.isBefore(now)){
            map.put("seasonEnd", true);
            return map;
        }
        map.put("seasonEnd", false);
        return map;
    }

    public Map<String, Object> SeasonResetStart(Map<String, Object> map) {
        ArenaSeasonResetData arenaSeasonResetData = arenaSeasonResetDataRepository.findById(1).orElse(null);
        if(arenaSeasonResetData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonResetData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonResetData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        arenaSeasonResetData.StartArenaSeasonReset();

        map.put("arenaSeasonResetData", arenaSeasonResetData);
        return map;
    }

    public Map<String, Object> SeasonReset(Map<String, Object> map) {

        //신규 시즌 정보 셋팅 및 추가
        long seasonInfosCount = arenaSeasonInfoDataRepository.count();
        int previousSeasonId = (int)seasonInfosCount;
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo(previousSeasonId)
                .orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: ArenaSeasonInfoData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ArenaSeasonInfoData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int nowSeasonNo = arenaSeasonInfoData.getNowSeasonNo();
        int newSeasonNo = nowSeasonNo + 1;
        ArenaSeasonInfoDataDto newArenaSeasonInfo = new ArenaSeasonInfoDataDto();
        newArenaSeasonInfo.setNowSeasonNo(newSeasonNo);
        StringMaker.Clear();
        StringMaker.stringBuilder.append("Season");
        StringMaker.stringBuilder.append(newSeasonNo);
        newArenaSeasonInfo.setSeasonName(StringMaker.stringBuilder.toString());
        LocalDateTime now = LocalDateTime.now();

        int dayOfWeek = now.getDayOfWeek().getValue();
        int gap = dayOfWeek - 1;
        LocalDateTime mondayDate = now.minusDays(gap);
        mondayDate = LocalDateTime.of(mondayDate.toLocalDate(), LocalTime.of(5,0,0));

        newArenaSeasonInfo.setSeasonStartTime(LocalDateTime.of(mondayDate.toLocalDate(), LocalTime.of(5,0,0)));
        newArenaSeasonInfo.setSeasonEndTime(LocalDateTime.of(newArenaSeasonInfo.getSeasonStartTime().plusDays(6).toLocalDate(), LocalTime.of(23,10,0)));
        ArenaSeasonInfoData newSeasonInfoData = newArenaSeasonInfo.ToEntity();
        newSeasonInfoData = arenaSeasonInfoDataRepository.save(newSeasonInfoData);

        List<RankingTierTable> rankingTierTableList = gameDataTableService.RankingTierTableList();
        List<RdsScore> thisSeasonRdsScoreList =  rdsScoreRepository.findAll();
        List<RdsScore> deleteList = thisSeasonRdsScoreList.stream().filter(i -> i.getArenaSeasonInfoId()!=nowSeasonNo).collect(Collectors.toList());
        thisSeasonRdsScoreList.removeAll(deleteList);
        thisSeasonRdsScoreList.sort((a,b) -> b.getScore().compareTo(a.getScore()));

        previousArenaSeasonRankingRepository.deleteAll();
        redisLongTemplate.opsForZSet().getOperations().delete(LeaderboardService.ALL_RANKING_LEADERBOARD);
        Iterable<RedisScore> redisScoreList = redisScoreRepository.findAll();

        redisScoreRepository.deleteAll(redisScoreList);
        long ranking = 1;
        //명예의 전당 셋팅
        List<HallofHonor> hallofHonorList = hallofHonorRepository.findAll();
        if(hallofHonorList != null && hallofHonorList.size() > 0) {
            int selectedIndex = hallofHonorList.size() - 1;
            HallofHonor hallofHonor = hallofHonorList.get(selectedIndex);
            hallofHonor.OffLatest();
        }

        RdsScore firstRdsScore = thisSeasonRdsScoreList.get(0);
        HallofHonorDto hallofHonorDto = new HallofHonorDto();
        hallofHonorDto.setHonorUserId(firstRdsScore.getUseridUser());
        hallofHonorDto.setUserGameName(firstRdsScore.getUserGameName());
        StringMaker.Clear();
        StringMaker.stringBuilder.append(arenaSeasonInfoData.getNowSeasonNo());
        hallofHonorDto.setSeasonCode(StringMaker.stringBuilder.toString());
        hallofHonorDto.setSelectedCharacterCode("hero");
        hallofHonorDto.setSeasonNo(arenaSeasonInfoData.getNowSeasonNo());
        hallofHonorDto.setSelectedPose(1);
        hallofHonorDto.setSelectedCostumeCode("");
        hallofHonorDto.setSelectedEquipmentArmorCode("");
        hallofHonorDto.setSelectedEquipmentHelmetCode("");
        hallofHonorDto.setSelectedEquipmentAccessoryCode("");
        hallofHonorDto.setChangedByUser(false);
        hallofHonorDto.setLatest(true);
        HallofHonor hallofHonor = hallofHonorDto.ToEntity();
        hallofHonorRepository.save(hallofHonor);

        for(RdsScore rdsScore : thisSeasonRdsScoreList) {
            if(rdsScore.isDummyUser()) {
                rdsScore.SetDummyUserSeasonInfoId(newSeasonNo);
                continue;
            }
            //직전 시즌 랭킹 정보 셋팅
            //현재 시즌에 참여했던 인원들만 백업 테이블에 기록한다.
            int previousSeasonInfoId = rdsScore.getArenaSeasonInfoId();
            if(previousSeasonInfoId == nowSeasonNo) {
                PreviousArenaSeasonRankingDto previousArenaSeasonRankingDto = new PreviousArenaSeasonRankingDto(0L, rdsScore.getUseridUser(), previousSeasonInfoId, rdsScore.getRankingtiertableId(), rdsScore.getScore(), ranking++);
                PreviousArenaSeasonRanking previousArenaSeasonRanking = previousArenaSeasonRankingDto.ToEntity();
                previousArenaSeasonRanking = previousArenaSeasonRankingRepository.save(previousArenaSeasonRanking);
            }

            //시즌 변경으로 인한 티어 및 점수 초기화 업데이트 진행
            int thisSeasonRankingTierId = rdsScore.getRankingtiertableId();
            int newSeasonRankingTierId = thisSeasonRankingTierId;
            if(newSeasonRankingTierId < 20) {
                newSeasonRankingTierId++;
            }
            int finalNewSeasonRankingTierId = newSeasonRankingTierId;
            RankingTierTable newRankingTierTable = rankingTierTableList.stream()
                    .filter(a -> a.getRankingtiertable_id() == finalNewSeasonRankingTierId)
                    .findAny()
                    .orElse(null);
            if(newRankingTierTable == null) {
                errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail -> Cause: Can't Find RankingTierTable. table_id => " + finalNewSeasonRankingTierId, this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail -> Cause: Can't Find RankingTierTable. table_id => " + finalNewSeasonRankingTierId, ResponseErrorCode.NOT_FIND_DATA);
            }
            int tierTotalPoint = newRankingTierTable.getTotalPoint();
            int tierMaxPoint = newRankingTierTable.getMaxPoint();
            Long needPointForTier = Long.valueOf(tierTotalPoint - tierMaxPoint);
            Long finalNewSeasonScore = needPointForTier;
            //최종 테이블 업데이트.
            rdsScore.refreshForNewSeason(finalNewSeasonScore, finalNewSeasonRankingTierId);
        }
        map.put("newSeasonInfoData", newSeasonInfoData);
        //
        return map;
    }

    public Map<String, Object> EndSeasonReset(Map<String, Object> map) {
        ArenaSeasonResetData arenaSeasonResetData = arenaSeasonResetDataRepository.findById(1).orElse(null);
        arenaSeasonResetData.EndArenaSeasonReset();

        map.put("arenaSeasonResetData", arenaSeasonResetData);
        return map;
    }
}
