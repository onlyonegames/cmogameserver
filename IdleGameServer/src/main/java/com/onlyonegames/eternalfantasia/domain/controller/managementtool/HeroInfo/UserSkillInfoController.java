package com.onlyonegames.eternalfantasia.domain.controller.managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.SetSkillInfoDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.UserSkillInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UserSkillInfoController {
    private final UserSkillInfoService userSkillInfoService;

    @PostMapping("/api/Test/UserSkillInfo")
    public ResponseDTO<Map<String, Object>> GetUserSkillInfo(@RequestBody AddUserIdDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userSkillInfoService.findByUseridUser(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/UserSkillInfo/SetUserSkillInfo")
    public ResponseDTO<Map<String, Object>> SetUserSkillInfo(@RequestBody SetSkillInfoDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = userSkillInfoService.setUserHeroSkill(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }
}
