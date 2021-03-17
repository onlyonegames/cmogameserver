package com.onlyonegames.eternalfantasia.domain.service.Managementtool.UserInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.UserDetailInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.UserInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyAttendanceDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyAttendanceData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.MyAttendanceDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;
    private final MyCharactersRepository myCharactersRepository;
    private final MyAttendanceDataRepository myAttendanceDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findAll(Map<String, Object> map) {
        List<UserInfoDto> userInfoDtos = new ArrayList<>();
        List<User> user = userRepository.findAll();
        for(User userarry : user) {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUserInfoDto(userarry);
            userInfoDtos.add(userInfoDto);
        }

        map.put("UserInfo", userInfoDtos);
        return map;
    }

    public Map<String, Object> findOneUser(Long userId, Map<String, Object> map) {
        UserInfoDto userInfoDto = new UserInfoDto();
        User user = userRepository.findById(userId).orElse(null);//.orElseThrow(() -> { errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(),"Fail! -> Cause: not find userId.",this.toString(),Thread.currentThread().getName()); throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);});
        if(user == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(),"[ManagementTool] Fail! -> Cause: not find userId.",this.getClass().getSimpleName(),Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        userInfoDto.setUserInfoDto(user);
        map.put("UserInfo", userInfoDto);
        return map;
    }

    public Map<String, Object> GetAttendanceInfo(Long userId, Map<String, Object> map) {
        MyAttendanceData myAttendanceData = myAttendanceDataRepository.findByUseridUser(userId).orElse(null);
        if(myAttendanceData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(),"[ManagementTool] Fail! -> Cause: MyAttendanceData not find.",this.getClass().getSimpleName(),Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyAttendanceDataDto myAttendanceDataDto = new MyAttendanceDataDto();
        myAttendanceDataDto.SetMyAttendanceDataDto(userId, myAttendanceData.getGettingCount(), myAttendanceData.getLastAttendanceDate(), myAttendanceData.isReceivableReward());

        map.put("myAttendanceData", myAttendanceDataDto);
        return map;
    }

    public Map<String, Object> SetAttendanceReward(Long userId, Map<String, Object> map){
        MyAttendanceData myAttendanceData = myAttendanceDataRepository.findByUseridUser(userId).orElse(null);
        if(myAttendanceData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(),"[ManagementTool] Fail! -> Cause: MyAttendanceData not find.",this.getClass().getSimpleName(),Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAttendanceData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myAttendanceData.SetReward();
        MyAttendanceDataDto myAttendanceDataDto = new MyAttendanceDataDto();
        myAttendanceDataDto.SetMyAttendanceDataDto(userId, myAttendanceData.getGettingCount(), myAttendanceData.getLastAttendanceDate(), myAttendanceData.isReceivableReward());

        map.put("myAttendanceData", myAttendanceDataDto);
        return map;
    }
}
