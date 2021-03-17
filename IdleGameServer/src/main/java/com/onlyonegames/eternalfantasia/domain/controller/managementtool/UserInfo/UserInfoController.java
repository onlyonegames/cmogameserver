package com.onlyonegames.eternalfantasia.domain.controller.managementtool.UserInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.UserInfo.UserInfoService;
import com.onlyonegames.eternalfantasia.domain.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class UserInfoController {
    private final UserInfoService userInfoService;
    private final UserService userService;

    @GetMapping("/api/Test/UserInfo")
    public ResponseDTO<Map<String, Object>> GetUserInfo() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userInfoService.findAll(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/UserInfo")
    public ResponseDTO<Map<String, Object>> FindOneUserInfo(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userInfoService.findOneUser(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/UserInfo/ResetUserName")
    public ResponseDTO<Map<String, Object>> ResetUserName(@RequestBody UserBaseDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userService.setUserName(dto.getId(),dto.getUserGameName(),map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/GetAttendanceInfo")
    public ResponseDTO<Map<String, Object>> GetAttendanceInfo(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userInfoService.GetAttendanceInfo(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/SetAttendanceReward")
    public ResponseDTO<Map<String, Object>> SetAttendceReward(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userInfoService.SetAttendanceReward(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
