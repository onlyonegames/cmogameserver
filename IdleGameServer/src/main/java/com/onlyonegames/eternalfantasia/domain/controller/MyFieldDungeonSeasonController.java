package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyFieldDungeonSeasonService;
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
public class MyFieldDungeonSeasonController {
    private final MyFieldDungeonSeasonService myFieldDungeonSeasonService;

    @GetMapping("/api/FieldDungeonSeason")
    public ResponseDTO<Map<String, Object>> GetSeasonInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myFieldDungeonSeasonService.GetSeasonInfo(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/FieldDungeonSeasonSetting")
    public ResponseDTO<Map<String, Object>> SeasonSetting() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myFieldDungeonSeasonService.SeasonSetting(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/FieldDungeonEndSeason")
    public ResponseDTO<Map<String, Object>> EndSeason() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myFieldDungeonSeasonService.EndSeason(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/ManagementToolFieldDungeonSeasonSetting")
    public ResponseDTO<Map<String, Object>> ManagementToolSeasonSetting() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myFieldDungeonSeasonService.ManagementToolSeasonSetting(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/ManagementToolFieldDungeonEndSeason")
    public ResponseDTO<Map<String, Object>> ManagementToolEndSeason() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myFieldDungeonSeasonService.ManagementToolEndSeason(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
