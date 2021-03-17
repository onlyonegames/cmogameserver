package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyArenaPlayLogForBattleRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyArenaPlayLogForBattleRecordController {
    private final MyArenaPlayLogForBattleRecordService myArenaPlayLogForBattleRecordService;

    /**내 전투 기록실 리스트 얻어오기 클라이언트 확인용*/
    @GetMapping("/api/MyArenaPlayLogForBattleRecordController/GetList")
    public ResponseDTO<Map<String, Object>> GetLog() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayLogForBattleRecordService.GetLog(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /**운영툴에서 각각 user별 확인을 위한 API*/
    @PostMapping("/api/GetArenaPlayLog")
    public ResponseDTO<Map<String, Object>> GetUserLog(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myArenaPlayLogForBattleRecordService.GetLog(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
