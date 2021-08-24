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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: PreviousWorldBossRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousWorldBossRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if (!previousWorldBossRanking.isReceivable()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: PreviousWorldBossRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousWorldBossRanking not find.", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
        }

        //TODO 랭킹 구간에 따른 차등 보상
        List<String> gettingItemList = new ArrayList<>();
        gettingItemList.add("diamond");
        gettingItemList.add("dragonCoin");
        String gettingItemCount;

        int myRanking = previousWorldBossRanking.getRanking();
        if (myRanking == 1) {
            gettingItemCount = "500";
        }
        else if (myRanking == 2) {
            gettingItemCount = "400";
        }
        else if (myRanking == 3) {
            gettingItemCount = "300";
        }
        else {
            long totalCount = previousWorldBossRankingRepository.count();

            int myPercent = Math.round(myRanking *100f / totalCount);
            if (myPercent <= 10) {
                gettingItemCount = "100";
            }
            else if (myPercent <= 30) {
                gettingItemCount = "80";
            }
            else if (myPercent <= 50) {
                gettingItemCount = "70";
            }
            else if (myPercent <= 70) {
                gettingItemCount = "60";
            }
            else if (myPercent <= 90) {
                gettingItemCount = "50";
            }
            else {
                gettingItemCount = "40";
            }
        }


        LocalDateTime now = LocalDateTime.now();
        for(String temp : gettingItemList){
            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            mailSendRequestDto.setTitle("월드 보스 " + previousWorldBossRanking.getRanking() + "등 보상 지급");
            mailSendRequestDto.setToId(userId);
            mailSendRequestDto.setGettingItem(temp);
            mailSendRequestDto.setGettingItemCount(gettingItemCount);
            mailSendRequestDto.setSendDate(now);
            mailSendRequestDto.setMailType(0);
            mailSendRequestDto.setExpireDate(now.plusDays(30));

            Map<String, Object> fakeMap = new HashMap<>();
            myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
        }
        previousWorldBossRanking.ReceiveReward();
        return map;
    }
}
