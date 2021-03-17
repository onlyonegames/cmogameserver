package com.onlyonegames.eternalfantasia.domain.controller.managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.HeroInfo.HeroStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class HeroStatusController {
    private final HeroStatusService heroStatusService;

    @PostMapping("/api/Test/HeroStatus")
    public ResponseDTO<Map<String, Object>> getHeroStatus(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        long userId = dto.getUserId();
        Map<String, Object> response = heroStatusService.getHeroStatus(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }
}
