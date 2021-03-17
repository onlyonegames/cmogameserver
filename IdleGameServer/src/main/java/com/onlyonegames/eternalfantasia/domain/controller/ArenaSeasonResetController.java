package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.ArenaSeasonResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ArenaSeasonResetController {
    private ArenaSeasonResetService arenaSeasonResetService;

    @GetMapping("/api/Test/SeasonReset")
    public ResponseDTO<Map<String, Object>> SeasonReset() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaSeasonResetService.SeasonReset(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/SeasonCheck")
    public ResponseDTO<Map<String, Object>> SeasonCheck() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaSeasonResetService.SeasonCheck(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/SeasonResetStart")
    public ResponseDTO<Map<String, Object>> SeasonResetStart() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaSeasonResetService.SeasonResetStart(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/EndSeasonReset")
    public ResponseDTO<Map<String, Object>> EndSeasonReset() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaSeasonResetService.EndSeasonReset(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
