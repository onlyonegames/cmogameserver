package com.onlyonegames.eternalfantasia.domain.controller.Contents;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ChallengeTowerFastClearRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Contents.ChallengeTowerPlayService;
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
public class ChallengeTowerPlayController {
    private final ChallengeTowerPlayService challengeTowerPlayService;

    @PostMapping("/api/Contents/ChallengeTowerFastClear")
    public ResponseDTO<Map<String, Object>> ChallengeTowerFastClear(@RequestBody ChallengeTowerFastClearRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = challengeTowerPlayService.ChallengeTowerFastClear(userId, dto.getCount(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Contents/ChallengeTowerClear")
    public ResponseDTO<Map<String, Object>> ChallengeTowerClear() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = challengeTowerPlayService.ChallengeTowerSuccess(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Contents/ChallengeTowerBestClear")
    public ResponseDTO<Map<String, Object>> ChallengeTowerBestClear(@RequestBody ChallengeTowerFastClearRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = challengeTowerPlayService.ChallengeTowerBestSuccess(userId, dto.getCount(), dto.getClassIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
