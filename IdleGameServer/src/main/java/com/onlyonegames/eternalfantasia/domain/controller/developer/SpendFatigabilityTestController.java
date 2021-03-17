package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class SpendFatigabilityTestController {
    private final SpendFatigabilityTestService spendFatigabilityTestService;

    @PostMapping("/api/SpendFatigability")
    public ResponseDTO<Map<String, Object>> SpendFatigability(@RequestBody FatigabilityTestDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = spendFatigabilityTestService.Spend(dto.getUserId(), dto.getHeroCode(), dto.getFatigability(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/AddFatigability")
    public ResponseDTO<Map<String, Object>> AddFatigability(@RequestBody FatigabilityTestDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = spendFatigabilityTestService.Add(dto.getUserId(), dto.getHeroCode(), dto.getFatigability(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
