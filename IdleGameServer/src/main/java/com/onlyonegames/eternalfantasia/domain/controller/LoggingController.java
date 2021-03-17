package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.LoggingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@AllArgsConstructor
public class LoggingController {
    private final LoggingService loggingService;

    @PostMapping("api/Test/Log")
    public ResponseDTO<Map<String, Object>> GetLog(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = loggingService.getLog(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "",true, response);
    }
}
