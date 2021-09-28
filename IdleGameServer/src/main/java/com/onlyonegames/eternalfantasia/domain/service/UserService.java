package com.onlyonegames.eternalfantasia.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onlyonegames.eternalfantasia.Interceptor.OnlyoneSession;
import com.onlyonegames.eternalfantasia.Interceptor.OnlyoneSessionRepository;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyAdventureStageDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyAttendanceDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyDayRewardDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyLevelRewardDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;

import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ErrorLoggingService errorLoggingService;
    private final GameDataTableService gameDataTableService;
    private final StandardTimeRepository standardTimeRepository;
    private final MyPassDataRepository myPassDataRepository;
    private final ServerStatusInfoRepository serverStatusInfoRepository;
    private final MyMailBoxService myMailBoxService;


    //세션 redis
    private final OnlyoneSessionRepository sessionRepository;

    public Map<String, Object> login(Long userId, String jwt, Map<String, Object> map) {
        ServerStatusInfo serverStatusInfo = serverStatusInfoRepository.getOne(1);
        User user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (serverStatusInfo.getServerStatus() == 1 && user.getUserType() == 1) {

            throw new MyCustomException("Server Check", ResponseErrorCode.UNDEFINED);
        }

        OnlyoneSession onlyoneSession = sessionRepository.findById(userId).orElse(null);
        if(onlyoneSession != null) {
            sessionRepository.delete(onlyoneSession);
        }
        onlyoneSession = new OnlyoneSession(userId, jwt);
        sessionRepository.save(onlyoneSession);

        if(user.isBlackUser()) {
            errorLoggingService.SetErrorLog(user.getId(), ResponseErrorCode.BLACK_USER.getIntegerValue(), "Black User", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Black User", ResponseErrorCode.BLACK_USER);
        }

        user.SetLastLoginDate();
        map.put("userInfo", user);
        map.put("serverTime", user.getLastloginDate());

        StandardTime standardTime = standardTimeRepository.findById(1).orElse(null);
        if(standardTime == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: StandardTime Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: StandardTime Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }

        map.put("standardTime", standardTime);

        MyPassData myPassData = myPassDataRepository.findByUseridUser(userId).orElse(null);
        if (myPassData == null) {
            MyDayRewardDataJsonDto myDayRewardDataJsonDto = new MyDayRewardDataJsonDto();
            MyAttendanceDataJsonDto myAttendanceDataJsonDto = new MyAttendanceDataJsonDto();
            MyLevelRewardDataJsonDto myLevelRewardDataJsonDto = new MyLevelRewardDataJsonDto();
            MyAdventureStageDataJsonDto myAdventureStageDataJsonDto = new MyAdventureStageDataJsonDto();
            myDayRewardDataJsonDto.Init();
            myAttendanceDataJsonDto.Init();
            myLevelRewardDataJsonDto.Init();
            myAdventureStageDataJsonDto.Init();
            String json_day = JsonStringHerlper.WriteValueAsStringFromData(myDayRewardDataJsonDto);
            String json_attendance = JsonStringHerlper.WriteValueAsStringFromData(myAttendanceDataJsonDto);
            String json_level = JsonStringHerlper.WriteValueAsStringFromData(myLevelRewardDataJsonDto);
            String json_stage = JsonStringHerlper.WriteValueAsStringFromData(myAdventureStageDataJsonDto);
            LocalDateTime now = LocalDateTime.now();
            myPassData = MyPassData.builder().useridUser(userId).json_daySaveData(json_day)
                    .json_attendanceSaveData(json_attendance).json_levelSaveData(json_level)
                    .lastAttendanceDate(LocalDateTime.of(now.minusDays(1).toLocalDate(), LocalTime.of(0, 0, 0)))
                    .json_stageSaveData(json_stage).gettingCount(0L).build();
            myPassData = myPassDataRepository.save(myPassData);
        }
        String json_saveData = myPassData.getJson_attendanceSaveData();
        MyAttendanceDataJsonDto myAttendanceDataJsonDto = JsonStringHerlper.ReadValueFromJson(json_saveData, MyAttendanceDataJsonDto.class);
        if (CheckAttendance(myPassData, myAttendanceDataJsonDto)) {
            //TODO 남은 보상 메일로 보상처리 보상 테이블 확인 필요
            Map<String, Object> tempMap = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            List<AttendanceFreePassTable> attendanceFreePassTableList = gameDataTableService.AttendanceFreePassTable();
            List<AttendanceBuyPassTable> attendanceBuyPassTableList = gameDataTableService.AttendanceBuyPassTable();
            for (int i = 0; i <31; i++) {
                if (!myAttendanceDataJsonDto.getRewardList().get(i)) {
                    AttendanceFreePassTable attendanceFreePassTable = attendanceFreePassTableList.get(i);
                    MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                    mailSendRequestDto.setToId(userId);
                    mailSendRequestDto.setSendDate(now);
                    mailSendRequestDto.setMailType(0);
                    mailSendRequestDto.setExpireDate(now.plusDays(30));
                    mailSendRequestDto.setTitle("미획득 출석보상 지급");
                    mailSendRequestDto.setGettingItem(attendanceFreePassTable.getRewardType()); //TODO 보상 테이블에 있는 보상으로 지급
                    mailSendRequestDto.setGettingItemCount(attendanceFreePassTable.getRewardCount());
                    myMailBoxService.SendMail(mailSendRequestDto, tempMap);
                }
                else
                    myAttendanceDataJsonDto.rewardList.set(i, false);
                if (myAttendanceDataJsonDto.isPassPurchase()) {
                    if (!myAttendanceDataJsonDto.getPassRewardList().get(i)) {
                        AttendanceBuyPassTable attendanceBuyPassTable = attendanceBuyPassTableList.get(i);
                        MailSendRequestDto mailSendRequestDto = new MailSendRequestDto();
                        mailSendRequestDto.setToId(userId);
                        mailSendRequestDto.setSendDate(now);
                        mailSendRequestDto.setMailType(0);
                        mailSendRequestDto.setExpireDate(now.plusDays(30));
                        mailSendRequestDto.setTitle("미획득 구매 출석보상 지급");
                        mailSendRequestDto.setGettingItem(attendanceBuyPassTable.getRewardType()); //TODO 보상 테이블에 있는 보상으로 지급
                        mailSendRequestDto.setGettingItemCount(attendanceBuyPassTable.getRewardCount());
                        myMailBoxService.SendMail(mailSendRequestDto, tempMap);
                    }
                    else
                        myAttendanceDataJsonDto.passRewardList.set(i, false);
                }
            }
        }
        json_saveData = JsonStringHerlper.WriteValueAsStringFromData(myAttendanceDataJsonDto);
        myPassData.ResetAttendanceJsonData(json_saveData);

        return map;
    }

    private Sort sortByBattleEndTime() {
        return Sort.by(Sort.Direction.DESC, "battleEndTime");
    }

    public Map<String, Object> setUserName(Long userId, String userName, Map<String, Object> map) {

        User findUser = userRepository.findById(userId).orElse(null);
        if(findUser == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
        }
        findUser.SetUserName(userName);
        return map;
    }

//    public Map<String, Object> IncreaseMaxInventorySlot(Long userId, Map<String, Object> map){
//
//        User findUser = userRepository.findById(userId).orElse(null);
//        if(findUser == null) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: user not find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: user not find", ResponseErrorCode.NOT_FIND_DATA);
//        }
//        //확장비용 체크
//        int cost = 30 + (((findUser.getHeroEquipmentInventoryMaxSlot() - 60) / 8) * 30);
//        if(!findUser.SpendDiamond(cost)) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NEED_MORE_DIAMOND.getIntegerValue(), "Fail -> Cause: Need More Diamond", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail -> Cause: Need More Diamond", ResponseErrorCode.NEED_MORE_DIAMOND);
//        }
//        if(!findUser.IncreaseSlot()) {
//            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY.getIntegerValue(), "Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), IS_DIRECT_WRIGHDB);
//            throw new MyCustomException("Fail! -> Cause: CANT_ANIMORE_INCREASE_INVENTORY", ResponseErrorCode.CANT_ANIMORE_INCREASE_INVENTORY);
//        }
//
//        map.put("user", findUser);
//        return map;
//    }
    /**
     * @return  boolean 값을 return true : 모든 아이템을 획득하도록 획득 서비스를 실행해야함
     */
    private boolean CheckAttendance(MyPassData myPassData, MyAttendanceDataJsonDto dto) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(myPassData.getLastAttendanceDate(), now);
        if(duration.toDays()>=1) {
            myPassData.ResetLastAttendanceDate(LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 0, 0)));
            return PlusCount(dto);
        }
        return false;
    }

    private boolean PlusCount(MyAttendanceDataJsonDto dto) {
        if(dto.gettingCount == 31) {
            dto.gettingCount = 1;
            return true;
        }
        else {
            dto.gettingCount += 1;
            return false;
        }
    }

}