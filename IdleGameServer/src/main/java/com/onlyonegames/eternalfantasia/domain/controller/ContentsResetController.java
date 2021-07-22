package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.Contents.ContentsResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ContentsResetController {
    private final ContentsResetService contentsResetService;

    @GetMapping("/api/Test/ResetChallengeTower")
    public ResponseDTO<Map<String, Object>> ResetChallengeTower() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = contentsResetService.ResetChallengeTower(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
