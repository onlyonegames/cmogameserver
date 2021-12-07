package com.onlyonegames.eternalfantasia.domain.controller.Test;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.VersionCheckService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class VersionCheckController {
    private final VersionCheckService versionCheckService;

    @GetMapping("/api/Test/ResetVersion")
    public ResponseDTO<Map<String, Object>> ResetVersion() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = versionCheckService.ResetVersion(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
