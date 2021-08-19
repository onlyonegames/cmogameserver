package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.StageRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.StageRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyContentsInfoRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.StageLeaderboardService;
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
public class StagePlayService {
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final StageRankingRepository stageRankingRepository;
    private final StageLeaderboardService stageLeaderboardService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> StageWin(Long userId, Map<String, Object> map) {
        MyContentsInfo myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
        if (myContentsInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: PreviousArenaRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousArenaRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myContentsInfo.ClearStage();

        int userPoint = 0;

        StageRanking stageRanking = stageRankingRepository.findByUseridUser(userId).orElse(null);
        if(stageRanking != null)
            userPoint = stageRanking.getPoint();

        userPoint++;

        userPoint = MathHelper.Clamp(userPoint, 0, Integer.MAX_VALUE);

        StageRanking saveRanking = stageLeaderboardService.setScore(userId, userPoint);
        Long changeRanking = stageLeaderboardService.getRank(userId);
        saveRanking.SetRanking(changeRanking.intValue());

        map.put("myContentsInfo", myContentsInfo);

        return map;
    }
}
