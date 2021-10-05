package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.MyWorldBossPlayDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Contents.WorldBossRankingDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Logging.WorldBossPlayLogDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.WorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyWorldBossPlayData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.WorldBossPlayLog;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyWorldBossPlayDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.WorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Logging.WorldBossPlayLogRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.WorldBossLeaderboardService;
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
public class WorldBossPlayService {
    private final WorldBossRankingRepository worldBossRankingRepository;
    private final ErrorLoggingService errorLoggingService;
    private final WorldBossLeaderboardService worldBossLeaderboardService;
    private final MyWorldBossPlayDataRepository myWorldBossPlayDataRepository;
    private final UserRepository userRepository;
    private final WorldBossPlayLogRepository worldBossPlayLogRepository;

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
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyWorldBossPlayData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyWorldBossPlayData", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found User", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(!myWorldBossPlayData.SpendPlayableCount()) {
            if(!user.SpendDiamond(600)) {
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "NEED_MORE_DIAMOND", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("NEED_MORE_DIAMOND", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
        }
        map.put("diamond", user.getDiamond());

        WorldBossPlayLogDto worldBossPlayLogDto = new WorldBossPlayLogDto();
        worldBossPlayLogDto.SetWorldBossPlayLogDto(userId);
        WorldBossPlayLog worldBossPlayLog = worldBossPlayLogRepository.save(worldBossPlayLogDto.ToEntity());
        myWorldBossPlayData.SetPlayLogId(worldBossPlayLog.getId());
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

        WorldBossRanking saveRanking = worldBossLeaderboardService.setScore(userId, damage, totalDamage);
        Long changeRanking = worldBossLeaderboardService.getRank(saveRanking.getUseridUser());
        saveRanking.ResetRanking(changeRanking);

        map.put("worldBossRanking", saveRanking);

        MyWorldBossPlayData myWorldBossPlayData = myWorldBossPlayDataRepository.findByUseridUser(userId).orElse(null);
        if(myWorldBossPlayData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found MyWorldBossPlayData", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found MyWorldBossPlayData", ResponseErrorCode.NOT_FIND_DATA);
        }

        WorldBossPlayLog worldBossPlayLog = worldBossPlayLogRepository.findById(myWorldBossPlayData.getPlayLogId()).orElse(null);
        if (worldBossPlayLog == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Not Found WorldBossPlayLog", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Not Found WorldBossPlayLog", ResponseErrorCode.NOT_FIND_DATA);
        }
        worldBossPlayLog.SetDamageAndTotalDamage(totalDamage, damage);

        return map;
    }
}
