package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AddGoldOrDiamondTestController {
    private final AddGoldOrDiamondTestService addGoldOrDiamondTestService;

    @PostMapping("/api/Test/AddDiamond")
    public ResponseDTO<Map<String, Object>> AddDiamond(@RequestBody AddGoldOrDiaTestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response =  addGoldOrDiamondTestService.AddDimaond(dto.getUserId(), dto.getAddCount(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/Test/AddGold")
    public ResponseDTO<Map<String, Object>> AddGold(@RequestBody AddGoldOrDiaTestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response =  addGoldOrDiamondTestService.AddGold(dto.getUserId(), dto.getAddCount(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
