package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousWorldBossRanking;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousWorldBossRankingRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class WorldBossRewardService {
    private final PreviousWorldBossRankingRepository previousWorldBossRankingRepository;
    private final MyMailBoxService myMailBoxService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetWorldBossReward(Long userId, Map<String, Object> map) {
        PreviousWorldBossRanking previousWorldBossRanking = previousWorldBossRankingRepository.findByUseridUser(userId).orElse(null);
        if (previousWorldBossRanking == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: PreviousWorldBossRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousWorldBossRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if (!previousWorldBossRanking.isReceivable()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: PreviousWorldBossRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousWorldBossRanking not find.", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
        }

        //TODO 랭킹 구간에 따른 차등 보상
        String gettingItemCount;

        int myRanking = previousWorldBossRanking.getRanking();
        if (myRanking == 1) {
            gettingItemCount = "1000";
        }
        else if (myRanking == 2) {
            gettingItemCount = "1000";
        }
        else if (myRanking == 3) {
            gettingItemCount = "1000";
        }
        else {
            long totalCount = previousWorldBossRankingRepository.count();

            int myPercent = Math.round(myRanking *100f / totalCount);
            if (myPercent <= 10) {
                gettingItemCount = "1000";
            }
            else if (myPercent <= 30) {
                gettingItemCount = "1000";
            }
            else if (myPercent <= 50) {
                gettingItemCount = "1000";
            }
            else if (myPercent <= 70) {
                gettingItemCount = "1000";
            }
            else if (myPercent <= 90) {
                gettingItemCount = "1000";
            }
            else {
                gettingItemCount = "1000";
            }
        }


        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setTitle("월드 보스 "+Integer.toBinaryString(previousWorldBossRanking.getRanking()) + "등 보상 지급");
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setGettingItem("diamond");
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(1);
        mailSendRequestDto.setExpireDate(now.plusDays(30));

        Map<String, Object> fakeMap = new HashMap<>();
        myMailBoxService.SendMail(mailSendRequestDto, fakeMap);

        return map;
    }
}
