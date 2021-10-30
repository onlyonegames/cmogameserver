package com.onlyonegames.eternalfantasia.domain.controller.Test;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Test.UserCheckService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserCheckController {
    private final UserCheckService userCheckService;

    @PostMapping("/api/Test/UserSkillPointCheck")
    public ResponseDTO<Map<String, Object>> UserSkillPointCheck(@RequestBody UserBaseDto dto){
        Map<String ,Object> map = new HashMap<>();
        Map<String, Object> response = userCheckService.UserSkillPointCheck(dto.getId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/PlayCount")
    public ResponseDTO<Map<String, Object>> PlayCount() {
        Map<String ,Object> map = new HashMap<>();
        Map<String, Object> response = userCheckService.WorldBossPlayCount(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

//    @GetMapping("/api/Test/Purchase")
//    public ResponseDTO<Map<String, Object>> Purchase() {
//        Map<String ,Object> map = new HashMap<>();
//        Map<String, Object> response = userCheckService.Price(map);
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }
}
