package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.StandardTimeResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class StandardTimeResetController {
    private final StandardTimeResetService standardTimeResetService;

    @GetMapping("/api/Test/StandardTimeReset")
    public ResponseDTO<Map<String, Object>> StandardTimeReset() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = standardTimeResetService.CheckTime(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    //by rainful for only test
    @GetMapping("/api/Test/Empty")
    public ResponseDTO<Map<String, Object>> Empty() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = map;
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
