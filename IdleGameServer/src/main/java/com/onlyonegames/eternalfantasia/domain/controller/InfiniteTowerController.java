package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.InfiniteTowerService;
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
public class InfiniteTowerController {

    private final InfiniteTowerService infiniteTowerService;

    @GetMapping("/api/InfinityTower/GetInfinityTowerRecordsList")
    public ResponseDTO<Map<String, Object>> GetInfinityTowerRecordsList() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = infiniteTowerService.GetInfinityTowerRecordsList(map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/InfinityTower/GetUserInfo")
    public ResponseDTO<Map<String, Object>> GetUserInfo(@RequestBody UserBaseDto userBaseDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = infiniteTowerService.GetUserInfo(userId, userBaseDto.getId(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
