package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.UpgradeStatusRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.MyForTestService;
import com.onlyonegames.eternalfantasia.domain.service.UpgradeStatusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class UpgradeStatusController {
    private final UpgradeStatusService upgradeStatusService;

    @PostMapping("/api/Test/UpgradeStatus")
    public ResponseDTO<Map<String, Object>> UpgradeStatus(@RequestBody UpgradeStatusRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = upgradeStatusService.UpgradeStatus(dto.getUserId(),dto.getAddLevel(),dto.getType(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
