package com.onlyonegames.eternalfantasia.domain.controller.managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.HeroEquipmentInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class HeroEquipmentInfoController {
    private final HeroEquipmentInfoService heroEquipmentInfoService;

    @PostMapping("/api/Test/UserEquipmentInfo")
    public ResponseDTO<Map<String, Object>> GetUserEquipmentInfo(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        long userId = dto.getUserId();
        Map<String, Object> response = heroEquipmentInfoService.findbyuserid(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

}
