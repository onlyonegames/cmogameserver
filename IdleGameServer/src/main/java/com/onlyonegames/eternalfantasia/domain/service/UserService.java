package com.onlyonegames.eternalfantasia.domain.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.MyArenaPlayData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.*;
import com.onlyonegames.eternalfantasia.domain.repository.*;

import com.onlyonegames.eternalfantasia.domain.repository.Contents.MyArenaPlayDataRepository;
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
    private final MyArenaPlayDataRepository myArenaPlayDataRepository;
    private final MyGachaInfoRepository myGachaInfoRepository;
    private final MyShopInfoRepository myShopInfoRepository;


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
            throw new MyCustomException("Server Check", ResponseErrorCode.SERVER_CHECK);
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

    public Map<String, Object> AccountLink(Long userId, String socialId, String socialProvider, Map<String, Object> map) {
        Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: userId Can't find", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA);
        }
        if (userRepository.existsBySocialId(socialId)) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.ALREADY_LINKED_ACCOUNT.getIntegerValue(), "Fail! -> Cause: ALREADY_LINKED_ACCOUNT", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: ALREADY_LINKED_ACCOUNT", ResponseErrorCode.ALREADY_LINKED_ACCOUNT);
        }
        if (!UUID_REGEX_PATTERN.matcher(user.getSocialId()).matches() && user.getSocialProvider().equals("google")) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_EXIST_CODE.getIntegerValue(), "Fail! -> Cause: NOT_EXIST_CODE", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: NOT_EXIST_CODE", ResponseErrorCode.NOT_EXIST_CODE);
        }
        user.ExchangeSocial(socialId, socialProvider);
        map.put("userInfo", user);
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