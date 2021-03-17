package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LearnLinkforceRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyLinkforceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyLinkforceInfoTestController {

    private final MyLinkforceInfoService myLinkforceInfoService;
    @Autowired
    public MyLinkforceInfoTestController(MyLinkforceInfoService myLinkforceInfoService) {
        this.myLinkforceInfoService = myLinkforceInfoService;
    }
    @PostMapping("/api/ResetLinkforce")
    public ResponseDTO<Map<String, Object>> ResetLinkforce(@RequestBody UserBaseDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myLinkforceInfoService.ResetLinkforce(dto.getId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
