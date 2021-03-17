package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ErrorLoggingDto;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@RestController
@AllArgsConstructor
public class ErrorLoggingController {
    private final ErrorLoggingService errorLoggingService;

    @PostMapping("/api/Test/ErrorLogging")
    public ResponseDTO<Map<String, Object>> SetErrorLog(@RequestBody ErrorLoggingDto dto) {
        Map<String, Object> map = new HashMap<>();
        errorLoggingService.SetErrorLog(dto.getUseridUser(), dto.getErrorCode(), dto.getErrorDetail(), dto.getErrorClass(), dto.getErrorFunction(), IS_DIRECT_WRIGHDB);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, map);
    }
}
