package com.onlyonegames.eternalfantasia.domain.service.Managementtool.Arena;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.ArenaSeasonInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyArenaSeasonSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.ArenaInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.ArenaSeasonInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.RankingTierTable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.ArenaSeasonInfoDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.RdsScoreRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyArenaSeasonSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyArenaPlayService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ArenaInfoService {
    private final MyArenaSeasonSaveDataRepository myArenaSeasonSaveDataRepository;
    private final ArenaSeasonInfoDataRepository arenaSeasonInfoDataRepository;
    private final RdsScoreRepository rdsScoreRepository;
    private final MyArenaPlayService myArenaPlayService;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> getMyArenaSeasonInfo(Long userId, Map<String, Object> map) {
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        List<RankingTierTable> rankingTierTableList = gameDataTableService.RankingTierTableList();
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.SetDto(myArenaSeasonSaveData);
        ArenaInfoDto arenaInfoDto = new ArenaInfoDto();
        arenaInfoDto.setArenaInfo(myArenaSeasonSaveData, getTierString(myArenaSeasonSaveData.getHighestRankingtiertable_id()), getTierString(myArenaSeasonSaveData.getRankingtiertable_id()));
        map.put("ArenaInfo",arenaInfoDto);
        return map;
    }

    public Map<String, Object> getArenaSeasonInfo(Map<String, Object> map) {
        long seasonCountLong = arenaSeasonInfoDataRepository.count();
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo((int)seasonCountLong).orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ArenaSeasonInfoDataDto arenaSeasonInfoDataDto = new ArenaSeasonInfoDataDto();
        arenaSeasonInfoDataDto.InitDbData(arenaSeasonInfoData);
        map.put("arenaSeasonInfo", arenaSeasonInfoDataDto);
        return map;
    }

    public Map<String, Object> SetArenaEndTime(LocalDateTime setTime, Map<String, Object> map) {
        long seasonCountLong = arenaSeasonInfoDataRepository.count();
        ArenaSeasonInfoData arenaSeasonInfoData = arenaSeasonInfoDataRepository.findByNowSeasonNo((int)seasonCountLong).orElse(null);
        if(arenaSeasonInfoData == null) {
            errorLoggingService.SetErrorLog(0L, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        arenaSeasonInfoData.SetSeasonEndTime(setTime);
        ArenaSeasonInfoDataDto arenaSeasonInfoDataDto = new ArenaSeasonInfoDataDto();
        arenaSeasonInfoDataDto.InitDbData(arenaSeasonInfoData);
        map.put("arenaSeasonInfo", arenaSeasonInfoDataDto);
        return map;
    }

    public Map<String, Object> SetNextTier(Long userId, Map<String, Object> map) {
        MyArenaSeasonSaveData myArenaSeasonSaveData = myArenaSeasonSaveDataRepository.findByUseridUser(userId).orElse(null);
        if(myArenaSeasonSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyArenaSeasonSaveData not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        RdsScore rdsScore = rdsScoreRepository.findByUseridUser(userId).orElse(null);
        if(rdsScore == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: RdsScore not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: RdsScore not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        int nowRankingTierId = myArenaSeasonSaveData.getRankingtiertable_id();
        if(nowRankingTierId == 21){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.UNDEFINED.getIntegerValue(), "[ManagementTool] Fail! -> Cause: Please play Arena once.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Please play Arena once.", ResponseErrorCode.UNDEFINED);
        }
        List<RankingTierTable> rankingTierTableList = gameDataTableService.RankingTierTableList();
        RankingTierTable rankingTierTable = rankingTierTableList.stream().filter(i -> i.getRankingtiertable_id()==nowRankingTierId).findAny().orElse(null);
        if(rankingTierTable == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: RankingTierTable not find UserId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: RankingTierTable not find UserId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myArenaSeasonSaveData.getRankingtiertable_id()!=1){
            String[] pointList = rankingTierTable.getPointScope().split("~");
            long tierMaxPoint = Long.parseLong(pointList[1]);
            myArenaSeasonSaveData.SetScore(tierMaxPoint);
            rdsScore.refreshForNewSeason(tierMaxPoint, nowRankingTierId);
            if (myArenaSeasonSaveData.getHighestRankingtiertable_id() != nowRankingTierId)
                myArenaSeasonSaveData.SetHighestRanking(nowRankingTierId);
            if (myArenaSeasonSaveData.getPreviousTierId() <= nowRankingTierId && myArenaSeasonSaveData.getPreviousTierId() != 20)
                myArenaSeasonSaveData.SetPreviousTier(nowRankingTierId + 1);
        }
        MyArenaSeasonSaveDataDto myArenaSeasonSaveDataDto = new MyArenaSeasonSaveDataDto();
        myArenaSeasonSaveDataDto.InitFromDbData(myArenaSeasonSaveData);
        map.put("myArenaSeasonSaveData", myArenaSeasonSaveDataDto);
        return map;
    }

    public Map<String, Object> PlayArenaWin(Long userId, Map<String, Object> map) {
        myArenaPlayService.ArenaWin(userId, map);
        return map;
    }

    String getTierString(int tier) {
        switch(tier) {
            case 1:
                return "플레티넘 1등급";
            case 2:
                return "플레티넘 2등급";
            case 3:
                return "플레티넘 3등급";
            case 4:
                return "플레티넘 4등급";
            case 5:
                return "플레티넘 5등급";
            case 6:
                return "골드 1등급";
            case 7:
                return "골드 2등급";
            case 8:
                return "골드 3등급";
            case 9:
                return "골드 4등급";
            case 10:
                return "골드 5등급";
            case 11:
                return "실버 1등급";
            case 12:
                return "실버 2등급";
            case 13:
                return "실버 3등급";
            case 14:
                return "실버 4등급";
            case 15:
                return "실버 5등급";
            case 16:
                return "브론즈 1등급";
            case 17:
                return "브론즈 2등급";
            case 18:
                return "브론즈 3등급";
            case 19:
                return "브론즈 4등급";
            case 20:
                return "브론즈 5등급";
            case 21:
                return "없음";
        }
        return "없음";
    }
}
