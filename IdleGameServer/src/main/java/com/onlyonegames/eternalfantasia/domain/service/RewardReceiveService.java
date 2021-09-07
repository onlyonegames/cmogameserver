package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyAdventureStageDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyAttendanceDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyDayRewardDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyLevelRewardDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyPassData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.MyPassDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class RewardReceiveService {
    private final MyPassDataRepository myPassDataRepository;
    private final UserRepository userRepository;
    private final MyMailBoxService myMailBoxService;
    private final ErrorLoggingService errorLoggingService;
    private final GameDataTableService gameDataTableService;

    public Map<String, Object> GetReward(Long userId, int rewardType, boolean passReward, int levelIndex, int index, Map<String, Object> map) {
        MyPassData myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
        if(myPassData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAttendanceData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        Map<String, Object> tempMap = new HashMap<>();

        switch(rewardType) {
            case 1: //일일보상
                String json_day = myPassData.getJson_daySaveData();
                MyDayRewardDataJsonDto myDayRewardDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_day, MyDayRewardDataJsonDto.class);
                if (passReward) {
                    if (!myDayRewardDataJsonDto.ReceiveADReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<DayADPassTable> dayADPassTableList = gameDataTableService.DayADPassTable();
                    DayADPassTable dayADPassTable = dayADPassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if(dayADPassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: DayADPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: DayADPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "일일광고보상지급", dayADPassTable.getRewardType(), dayADPassTable.getRewardCount(), tempMap);
                }
                else {
                    if (!myDayRewardDataJsonDto.ReceiveReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<DayFreePassTable> dayFreePassTableList = gameDataTableService.DayFreePassTable();
                    DayFreePassTable dayFreePassTable = dayFreePassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if(dayFreePassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: DayFreePassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: DayFreePassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "일일일보상지급", dayFreePassTable.getRewardType(), dayFreePassTable.getRewardCount(), tempMap);
                }
                json_day = JsonStringHerlper.WriteValueAsStringFromData(myDayRewardDataJsonDto);
                myPassData.ResetDayJsonData(json_day);
                break;
            case 2: //출석보상
                String json_attendance = myPassData.getJson_attendanceSaveData();
                MyAttendanceDataJsonDto myAttendanceDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_attendance, MyAttendanceDataJsonDto.class);
                if (passReward) {
                    if (!myAttendanceDataJsonDto.isPassPurchase()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: NOT_EXIST_CODE", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: NOT_EXIST_CODE", ResponseErrorCode.NOT_EXIST_CODE);
                    }
                    if (!myAttendanceDataJsonDto.ReceivePassReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<AttendanceBuyPassTable> attendanceBuyPassTableList = gameDataTableService.AttendanceBuyPassTable();
                    AttendanceBuyPassTable attendanceBuyPassTable = attendanceBuyPassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if (attendanceBuyPassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: AttendanceBuyPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: AttendanceBuyPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "출석패스보상지급", "diamond", "1", tempMap);
                }
                else {
                    if (!myAttendanceDataJsonDto.ReceiveReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<AttendanceFreePassTable> attendanceFreePassTableList = gameDataTableService.AttendanceFreePassTable();
                    AttendanceFreePassTable attendanceFreePassTable = attendanceFreePassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if (attendanceFreePassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: AttendanceFreePassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: AttendanceFreePassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "출석보상지급", attendanceFreePassTable.getRewardType(), attendanceFreePassTable.getRewardCount(), tempMap);
                }
                json_attendance = JsonStringHerlper.WriteValueAsStringFromData(myAttendanceDataJsonDto);
                myPassData.ResetAttendanceJsonData(json_attendance);
                break;
            case 3: //레벨 보상
                String json_level = myPassData.getJson_levelSaveData();
                MyLevelRewardDataJsonDto myLevelRewardDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_level, MyLevelRewardDataJsonDto.class);
                MyLevelRewardDataJsonDto.LevelReward levelReward = myLevelRewardDataJsonDto.levelRewardList.get(levelIndex);
                if (passReward) {
                    if (!levelReward.passPurchase) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: NOT_EXIST_CODE", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: NOT_EXIST_CODE", ResponseErrorCode.NOT_EXIST_CODE);
                    }
                    if (!levelReward.ReceivePassReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<LevelBuyPassTable> levelBuyPassTableList = gameDataTableService.LevelBuyPassTable();
                    List<LevelBuyPassTable> levelBuyPassTableGroupList = levelBuyPassTableList.stream().filter(i -> i.getGroupIndex() == levelIndex).collect(Collectors.toList());
                    LevelBuyPassTable levelBuyPassTable = levelBuyPassTableGroupList.stream().filter(i -> i.getGroupId() == index).findAny().orElse(null);
                    if (levelBuyPassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: LevelBuyPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: LevelBuyPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "레벨패스보상지급", levelBuyPassTable.getRewardType(), levelBuyPassTable.getRewardCount(), tempMap);
                }
                else {
                    if (!levelReward.ReceiveReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<LevelFreePassTable> levelFreePassTableList = gameDataTableService.LevelFreePassTable();
                    List<LevelFreePassTable> levelFreePassTableGroupList = levelFreePassTableList.stream().filter(i -> i.getGroupIndex() == levelIndex).collect(Collectors.toList());
                    LevelFreePassTable levelFreePassTable = levelFreePassTableGroupList.stream().filter(i -> i.getGroupId() == index).findAny().orElse(null);
                    if (levelFreePassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: LevelBuyPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: LevelBuyPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "레벨보상지급", levelFreePassTable.getRewardType(), levelFreePassTable.getRewardCount(), tempMap);
                }
                json_level = JsonStringHerlper.WriteValueAsStringFromData(myLevelRewardDataJsonDto);
                myPassData.ResetLevelJsonData(json_level);
                break;
            case 4: //스테이지 보상
                String json_stage = myPassData.getJson_stageSaveData();
                MyAdventureStageDataJsonDto myAdventureStageDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_stage, MyAdventureStageDataJsonDto.class);
                if (passReward) {
                    if (!myAdventureStageDataJsonDto.isPassPurchase()) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: NOT_EXIST_CODE", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: NOT_EXIST_CODE", ResponseErrorCode.NOT_EXIST_CODE);
                    }
                    if (!myAdventureStageDataJsonDto.ReceivePassReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<AdventureStageBuyPassTable> adventureStageBuyPassTableList = gameDataTableService.AdventureStageBuyPassTable();
                    AdventureStageBuyPassTable adventureStageBuyPassTable = adventureStageBuyPassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if (adventureStageBuyPassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: AdventureStageBuyPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: AdventureStageBuyPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "스테이지패스보상지급", adventureStageBuyPassTable.getRewardType(), adventureStageBuyPassTable.getRewardCount(), tempMap);
                }
                else {
                    if (!myAdventureStageDataJsonDto.ReceiveReward(index)) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_RECEIVED_REWARD.getIntegerValue(), "Fail! -> Cause: Already Received Reward", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: Already Received Reward", ResponseErrorCode.ALREADY_RECEIVED_REWARD);
                    }
                    List<AdventureStageFreePassTable> adventureStageFreePassTableList = gameDataTableService.AdventureStageFreePassTable();
                    AdventureStageFreePassTable adventureStageFreePassTable = adventureStageFreePassTableList.stream().filter(i -> i.getId() == index).findAny().orElse(null);
                    if (adventureStageFreePassTable == null) {
                        errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: AdventureStageBuyPassTable Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
                        throw new MyCustomException("Fail! -> Cause: AdventureStageBuyPassTable Can't find", ResponseErrorCode.NOT_FIND_DATA);
                    }
                    SendMail(userId, "스테이지패스보상지급", adventureStageFreePassTable.getRewardType(), adventureStageFreePassTable.getRewardCount(), tempMap);
                }
                json_stage = JsonStringHerlper.WriteValueAsStringFromData(myAdventureStageDataJsonDto);
                myPassData.ResetStageSaveData(json_stage);
                break;
        }
        return map;
    }

    public Map<String, Object> Purchase(Long userId, int passType, int levelIndex, Map<String, Object> map){
        MyPassData myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
        if(myPassData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAttendanceData Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        String mailTitle = "";
        String gettingItemCount = "";
        switch(passType) {
            case 2: //출석보상
                String json_attendance = myPassData.getJson_attendanceSaveData();
                MyAttendanceDataJsonDto myAttendanceDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_attendance, MyAttendanceDataJsonDto.class);
                myAttendanceDataJsonDto.setPassPurchase(true);
                json_attendance = JsonStringHerlper.WriteValueAsStringFromData(myAttendanceDataJsonDto);
                myPassData.ResetAttendanceJsonData(json_attendance);
                mailTitle = "출석 패스 구매 다이아 지급";
                gettingItemCount = "10000";
                break;
            case 3: //레벨 보상
                String json_level = myPassData.getJson_levelSaveData();
                MyLevelRewardDataJsonDto myLevelRewardDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_level, MyLevelRewardDataJsonDto.class);

                MyLevelRewardDataJsonDto.LevelReward previousLevelReward = null;
                MyLevelRewardDataJsonDto.LevelReward levelReward = null;
                if (levelIndex>0) {
                    previousLevelReward = myLevelRewardDataJsonDto.levelRewardList.get(levelIndex-1);
                    levelReward = myLevelRewardDataJsonDto.levelRewardList.get(levelIndex);
                    if (!previousLevelReward.passPurchase) {
                        //TODO Error
                    }
                    else
                        levelReward.PurchasePass();
                }
                else {
                    levelReward = myLevelRewardDataJsonDto.levelRewardList.get(levelIndex);
                    levelReward.PurchasePass();
                }
                json_level = JsonStringHerlper.WriteValueAsStringFromData(myLevelRewardDataJsonDto);
                myPassData.ResetLevelJsonData(json_level);
                mailTitle = "레벨 패스 구매 다이아 지급";
                gettingItemCount = "10000";
                break;
            case 4: //스테이지 보상
                String json_stage = myPassData.getJson_stageSaveData();
                MyAdventureStageDataJsonDto myAdventureStageDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_stage, MyAdventureStageDataJsonDto.class);
                myAdventureStageDataJsonDto.setPassPurchase(true);
                json_stage = JsonStringHerlper.WriteValueAsStringFromData(myAdventureStageDataJsonDto);
                myPassData.ResetStageSaveData(json_stage);
                mailTitle = "스테이지 패스 구매 다이아 지급";
                gettingItemCount = "10000";
                break;
        }
        Map<String, Object> tempMap = new HashMap<>();
        SendMail(userId, mailTitle, "diamond", gettingItemCount, tempMap);
        return map;
    }

    private void SendMail(Long userId, String title, String gettingItem, String gettingItemCount, Map<String, Object> tempMap) {
        LocalDateTime now = LocalDateTime.now();
        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
        mailSendRequestDto.setToId(userId);
        mailSendRequestDto.setSendDate(now);
        mailSendRequestDto.setMailType(1);
        mailSendRequestDto.setExpireDate(now.plusDays(30));
        mailSendRequestDto.setTitle(title);
        mailSendRequestDto.setGettingItem(gettingItem); //TODO 보상 테이블에 있는 보상으로 지급
        mailSendRequestDto.setGettingItemCount(gettingItemCount);
        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
    }
}