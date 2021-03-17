package com.onlyonegames.eternalfantasia.domain.controller.managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.UserDetailInfoDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.HeroLevelInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class HeroLevelInfoController {
    private final HeroLevelInfoService heroLevelInfoService;

    @PostMapping("/api/Test/HeroLevelInfo")
    public ResponseDTO<Map<String, Object>> findUserLevelInfo(@RequestBody AddUserIdDto dto){
        Map<String, Object> map = new HashMap<>();
        long userId = dto.getUserId();
        Map<String, Object> response = heroLevelInfoService.findUserLevelInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/HeroLevelInfo/SetUserLevelInfo")
    public ResponseDTO<Map<String, Object>> setUserLevelInfo(@RequestBody UserDetailInfoDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = heroLevelInfoService.setUserLevelInfo(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }
}
