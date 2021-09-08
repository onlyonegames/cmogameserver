package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousBattlePowerRanking;
import com.onlyonegames.eternalfantasia.domain.repository.Contents.PreviousBattlePowerRankingRepository;
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
public class BattlePowerRewardService {
    private final PreviousBattlePowerRankingRepository previousBattlePowerRankingRepository;
    private final MyMailBoxService myMailBoxService;
    private final ErrorLoggingService errorLoggingService;
    
    public Map<String, Object> GetBattlePowerReward(Long userId, Map<String, Object> map) {
        PreviousBattlePowerRanking previousBattlePowerRanking = previousBattlePowerRankingRepository.findByUseridUser(userId).orElse(null);
        if(previousBattlePowerRanking == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: PreviousBattlePowerRanking not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: PreviousBattlePowerRanking not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(!previousBattlePowerRanking.isReceivable()) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: Already Received Reward.", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
        }

        List<String> gettingItemList = new ArrayList<>();
        gettingItemList.add("diamond");
        String gettingItemCount;

        int myRanking = previousBattlePowerRanking.getRanking();
        if (myRanking == 1) {
            gettingItemCount = "10000";
        }
        else if (myRanking == 2) {
            gettingItemCount = "8000";
        }
        else if (myRanking == 3) {
            gettingItemCount = "6500";
        }
        else {
            long totalCount = previousBattlePowerRankingRepository.count();

            int myPercent = Math.round(myRanking *100f / totalCount);
            if (myPercent <= 10) {
                gettingItemCount = "5000";
            }
            else if (myPercent <= 30) {
                gettingItemCount = "4500";
            }
            else if (myPercent <= 50) {
                gettingItemCount = "4000";
            }
            else if (myPercent <= 70) {
                gettingItemCount = "3500";
            }
            else if (myPercent <= 90) {
                gettingItemCount = "3000";
            }
            else {
                gettingItemCount = "2500";
            }
        }


        LocalDateTime now = LocalDateTime.now();
        for (String temp : gettingItemList) {
            MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
            mailSendRequestDto.setTitle("전투력 "+ myRanking + "등 보상 지급");
            mailSendRequestDto.setToId(userId);
            mailSendRequestDto.setGettingItem(temp);
            mailSendRequestDto.setGettingItemCount(gettingItemCount);
            mailSendRequestDto.setSendDate(now);
            mailSendRequestDto.setMailType(0);
            mailSendRequestDto.setExpireDate(now.plusDays(30));

            Map<String, Object> fakeMap = new HashMap<>();
            myMailBoxService.SendMail(mailSendRequestDto, fakeMap);
        }
        previousBattlePowerRanking.ReceiveReward();
        return map;
    }
}
