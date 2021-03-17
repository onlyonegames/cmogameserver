package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.AncientDragonPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.OrdealDungeonPlayRequestCommonDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyAncientDragonPlayService;
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
public class MyAncientDragonPlayController {
    private final MyAncientDragonPlayService myAncientDragonPlayService;

    @GetMapping("/api/AncientDragonPlayTimeSet")
    public ResponseDTO<Map<String, Object>> OrdealDungeonPlayTimeSet() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAncientDragonPlayService.AncientDragonPlayTimeSet(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AncientDragonPlay")
    public ResponseDTO<Map<String, Object>> Play(@RequestBody AncientDragonPlayRequestCommonDto ancientDragonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAncientDragonPlayService.StartAncientDragonPlay(userId, ancientDragonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AncientDragonClear")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody AncientDragonPlayRequestCommonDto ancientDragonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAncientDragonPlayService.AncientDragonClear(userId, ancientDragonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AncientDragonFail")
    public ResponseDTO<Map<String, Object>> Fail(@RequestBody AncientDragonPlayRequestCommonDto ancientDragonPlayRequestCommonDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myAncientDragonPlayService.AncientDragonFail(userId, ancientDragonPlayRequestCommonDto.getFloorNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
