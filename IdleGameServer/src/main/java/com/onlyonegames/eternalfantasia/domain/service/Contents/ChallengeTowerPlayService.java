package com.onlyonegames.eternalfantasia.domain.service.Contents;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import com.onlyonegames.eternalfantasia.domain.model.entity.StandardTime;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.MyContentsInfoRepository;
import com.onlyonegames.eternalfantasia.domain.repository.StandardTimeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Contents.Leaderboard.*;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class ChallengeTowerPlayService {
    private final MyContentsInfoRepository myContentsInfoRepository;
    private final StandardTimeRepository standardTimeRepository;
    private final ErrorLoggingService errorLoggingService;
    private final UserRepository userRepository;
    private final WarriorChallengeTowerLeaderboardService warriorChallengeTowerLeaderboardService;
    private final ThiefChallengeTowerLeaderboardService thiefChallengeTowerLeaderboardService;
    private final KnightChallengeTowerLeaderboardService knightChallengeTowerLeaderboardService;
    private final ArcherChallengeTowerLeaderboardService archerChallengeTowerLeaderboardService;
    private final MagicianChallengeTowerLeaderboardService magicianChallengeTowerLeaderboardService;

    public Map<String, Object> ChallengeTowerFastClear(Long userId, int count, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (!user.SpendChallengeTicket(count)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_CHALLENGE_TICKET.getIntegerValue(), "NEED_MORE_CHALLENGE_TICKET", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("NEED_MORE_CHALLENGE_TICKET", ResponseErrorCode.NEED_MORE_CHALLENGE_TICKET);
        }
        map.put("challengeTicket", user.getChallengeTicket());
        return map;
    }

    public Map<String, Object> ChallengeTowerSuccess(Long userId, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyContentsInfo myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
        if (myContentsInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyContentsInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (!user.SpendChallengeTicket(1)) {
            int spendDiamond;
            switch(myContentsInfo.getChallengeTowerDiamondCount()) {
                case 0:
                    spendDiamond = 10000;
                    break;
                case 1:
                    spendDiamond = 50000;
                    break;
                case 2:
                    spendDiamond = 100000;
                    break;
                case 3:
                    spendDiamond = 180000;
                    break;
                case 4:
                    spendDiamond = 300000;
                    break;
                default:
                    if (myContentsInfo.getChallengeTowerDiamondCount() >= 5)
                        spendDiamond = 300000;
                    else {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyContentsInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    break;
            }
            if (!user.SpendDiamond(spendDiamond)){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "NEED_MORE_DIAMOND", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("NEED_MORE_DIAMOND", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            myContentsInfo.DiamondEntry();
            map.put("diamond", user.getDiamond());
        }

        map.put("challengeTicket", user.getChallengeTicket());
        return map;
    }

    public Map<String, Object> ChallengeTowerBestSuccess(Long userId, int floor, int classIndex, Map<String, Object> map) {
        MyContentsInfo myContentsInfo = myContentsInfoRepository.findByUseridUser(userId).orElse(null);
        if (myContentsInfo == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyContentsInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        if (standardTime == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: StandardTime not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: StandardTime not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: User not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (!user.SpendChallengeTicket(1)) {
            int spendDiamond;
            switch(myContentsInfo.getChallengeTowerDiamondCount()) {
                case 0:
                    spendDiamond = 10000;
                    break;
                case 1:
                    spendDiamond = 50000;
                    break;
                case 2:
                    spendDiamond = 100000;
                    break;
                case 3:
                    spendDiamond = 180000;
                    break;
                case 4:
                    spendDiamond = 300000;
                    break;
                default:
                    if (myContentsInfo.getChallengeTowerDiamondCount() >= 5)
                        spendDiamond = 300000;
                    else {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyContentsInfo not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: MyContentsInfo not find.", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    break;
            }
            if (!user.SpendDiamond(spendDiamond)){
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "NEED_MORE_DIAMOND", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("NEED_MORE_DIAMOND", ResponseErrorCode.NEED_MORE_DIAMOND);
            }
            myContentsInfo.DiamondEntry();
            map.put("diamond", user.getDiamond());
        }

//        if (myContentsInfo.getChallengeTowerFloor() >= 50) {
//            //TODO Error 추가
//        }

        switch (classIndex) {
            case 1:
                myContentsInfo.SetWarriorChallengeTowerFloor(floor);
                warriorChallengeTowerLeaderboardService.setScore(userId, floor);
                map.put("warriorChallengeTowerFloor", myContentsInfo.getWarriorChallengeTowerFloor());
                break;
            case 2:
                myContentsInfo.SetThiefChallengeTowerFloor(floor);
                thiefChallengeTowerLeaderboardService.setScore(userId, floor);
                map.put("thiefChallengeTowerFloor", myContentsInfo.getThiefChallengeTowerFloor());
                break;
            case 3:
                myContentsInfo.SetKnightChallengeTowerFloor(floor);
                knightChallengeTowerLeaderboardService.setScore(userId, floor);
                map.put("knightChallengeTowerFloor", myContentsInfo.getKnightChallengeTowerFloor());
                break;
            case 4:
                myContentsInfo.SetArcherChallengeTowerFloor(floor);
                archerChallengeTowerLeaderboardService.setScore(userId, floor);
                map.put("archerChallengeTowerFloor", myContentsInfo.getArcherChallengeTowerFloor());
                break;
            case 5:
                myContentsInfo.SetMagicianChallengeTowerFloor(floor);
                magicianChallengeTowerLeaderboardService.setScore(userId, floor);
                map.put("magicianChallengeTowerFloor", myContentsInfo.getMagicianChallengeTowerFloor());
                break;
            default:
                errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: StandardTime not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: StandardTime not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        map.put("challengeTicket", user.getChallengeTicket());
        return map;
    }
}
