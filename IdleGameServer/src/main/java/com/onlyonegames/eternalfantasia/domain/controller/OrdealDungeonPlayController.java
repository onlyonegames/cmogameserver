package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.HeroTowerPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.OrdealDungeonPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyOrdealDungeonPlayService;
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
public class OrdealDungeonPlayController {
    private final MyOrdealDungeonPlayService myOrdealDungeonPlayService;

    @GetMapping("/api/OrdealDungeonPlayTimeSet")
    public ResponseDTO<Map<String, Object>> OrdealDungeonPlayTimeSet() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myOrdealDungeonPlayService.OrdealDungeonPlayTimeSet(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/OrdealDungeonPlay")
    public ResponseDTO<Map<String, Object>> Play(@RequestBody OrdealDungeonPlayRequestCommonDto ordealDungeonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myOrdealDungeonPlayService.StartOrdealDungeonPlay(userId, ordealDungeonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/OrdealDungeonClear")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody OrdealDungeonPlayRequestCommonDto ordealDungeonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myOrdealDungeonPlayService.OrdealDungeonClear(userId, ordealDungeonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/OrdealDungeonFail")
    public ResponseDTO<Map<String, Object>> Fail(@RequestBody OrdealDungeonPlayRequestCommonDto ordealDungeonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myOrdealDungeonPlayService.OrdealDungeonFail(userId, ordealDungeonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
