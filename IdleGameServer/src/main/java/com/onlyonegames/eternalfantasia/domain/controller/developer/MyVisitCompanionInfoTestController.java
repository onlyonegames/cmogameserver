package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.developer.MyVisitCompanionInfoServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyVisitCompanionInfoTestController {
    private final MyVisitCompanionInfoServiceTest myVisitCompanionInfoService;

    @Autowired
    public MyVisitCompanionInfoTestController(MyVisitCompanionInfoServiceTest myVisitCompanionInfoService) {
        this.myVisitCompanionInfoService = myVisitCompanionInfoService;
    }

    @GetMapping("/api/ClearInfo")
    public ResponseDTO<Map<String, Object>> GetMyVisitCompanionInfo(@RequestBody UserBaseDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myVisitCompanionInfoService.ClearInfo(dto.getId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
