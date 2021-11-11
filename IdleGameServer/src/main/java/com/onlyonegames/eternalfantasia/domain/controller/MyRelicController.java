package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RelicUpgradeRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyRelicService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyRelicController {
    private final MyRelicService myRelicService;

    @PostMapping("/api/Inventory/UpgradeRelic")
    public ResponseDTO<Map<String, Object>> UpgradeRelic(@RequestBody RelicUpgradeRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myRelicService.UpgradeRelic(userId, dto.rune_Id, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Inventory/UpgradeAllRelic")
    public ResponseDTO<Map<String, Object>> UpgradeAllRelic(@RequestBody RelicUpgradeRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myRelicService.UpgradeAllRelic(userId, dto.rune_Id, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
