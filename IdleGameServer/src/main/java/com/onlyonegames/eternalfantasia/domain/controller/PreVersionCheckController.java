package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PreVersionCheckRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.VersionCheckService;
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
public class PreVersionCheckController {
    private final VersionCheckService versionCheckService;

    @PostMapping("/api/Test/PreVersionCheck")
    public ResponseDTO<Map<String, Object>> PreVersionCheck(@RequestBody PreVersionCheckRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = versionCheckService.PreVersionCheck(dto.getVersion(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
