package com.onlyonegames.eternalfantasia.domain.controller.managementtool.FieldDungeon;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ArenaSeasonEndTimeSetRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.FieldDungeon.FieldDungeonInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FieldDungeonInfoController {
    private final FieldDungeonInfoService fieldDungeonInfoService;

    @GetMapping("/api/Test/GetFieldDungeonSeasonInfo")
    public ResponseDTO<Map<String, Object>> GetFieldDungeonSeasonInfo() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fieldDungeonInfoService.GetFieldDungeonInfo(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/SetFieldDungeonSeasonEndTime")
    public ResponseDTO<Map<String, Object>> SetSeasonEndTime(@RequestBody ArenaSeasonEndTimeSetRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fieldDungeonInfoService.SetDungeonEndTime(dto.getSetTime(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/AddFieldDungeonPlayable")
    public ResponseDTO<Map<String, Object>> AddFieldDungeonPlayable(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = fieldDungeonInfoService.AddPlayableCount(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
