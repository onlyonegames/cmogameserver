package com.onlyonegames.eternalfantasia.domain.controller.Contents;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Test.userIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Contents.ArenaMatchingService;
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
public class ArenaMatchingController {
    private final ArenaMatchingService arenaMatchingService;

    @GetMapping("/api/Contents/Arena/Matching")
    public ResponseDTO<Map<String, Object>> GetReadyVersus() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = arenaMatchingService.GetReadyVersus(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/Arena/ForceMatching")
    public ResponseDTO<Map<String, Object>> ForceMatching() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = arenaMatchingService.ForceGetReadyVersus(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/Arena/Matching")
    public ResponseDTO<Map<String, Object>> TestGetReadyVersus(@RequestBody userIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = arenaMatchingService.GetReadyVersus(dto.getUserid(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
