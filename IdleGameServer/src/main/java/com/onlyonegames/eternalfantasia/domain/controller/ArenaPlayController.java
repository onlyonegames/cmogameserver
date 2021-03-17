package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ArenaPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyArenaPlayService;
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
public class ArenaPlayController {
    private final MyArenaPlayService myArenaPlayService;

    @GetMapping("/api/ArenaPlayTimeSet")
    public ResponseDTO<Map<String, Object>> ArenaPlayTimeSet() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayService.ArenaPlayTimeSet(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/ArenaPlay")
    public ResponseDTO<Map<String, Object>> Play(@RequestBody ArenaPlayRequestCommonDto arenaPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayService.ArenaPlay(userId, arenaPlayRequestCommonDto.getSelecteMatchUserId(), arenaPlayRequestCommonDto.getEnemyTeamBattlePower(), arenaPlayRequestCommonDto.getMyTeamBattlePower(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/ArenaClear")
    public ResponseDTO<Map<String, Object>> Clear() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayService.ArenaWin(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/ArenaFail")
    public ResponseDTO<Map<String, Object>> Fail() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayService.ArenaFail(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    @GetMapping("/api/DirectBuyArenaTicket")
    public ResponseDTO<Map<String, Object>> DirectBuyArenaTicket() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myArenaPlayService.DirectBuyArenaTicket(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
