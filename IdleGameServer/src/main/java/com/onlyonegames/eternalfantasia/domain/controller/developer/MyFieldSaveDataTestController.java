package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.MyNewFieldSaveDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyFieldSaveDataTestController {
    private final MyNewFieldSaveDataService myNewFieldSaveDataService;

    @PostMapping("/api/Test/ResetNewFieldObject")
    public ResponseDTO<Map<String, Object>> ResetFieldSaveData(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myNewFieldSaveDataService.Reset(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*2020-09-02 by rainful 핫타임 오브젝트 클리어 추가*/
    @PostMapping("/api/Test/ResetHotTimeFieldObject")
    public ResponseDTO<Map<String, Object>> ResetHotTimeFieldObject(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myNewFieldSaveDataService.HotTimeReset(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
