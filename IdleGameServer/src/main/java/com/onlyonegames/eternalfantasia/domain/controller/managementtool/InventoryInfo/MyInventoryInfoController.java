package com.onlyonegames.eternalfantasia.domain.controller.managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo.MyInventoryInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyInventoryInfoController {
    private final MyInventoryInfoService myInventoryInfoService;

    @PostMapping("/api/Test/MyInventoryInfo")
    public ResponseDTO<Map<String, Object>> GetMyInventoryInfo(@RequestBody AddUserIdDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myInventoryInfoService.findUserInventory(dto.getUserId(), map);

        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }

    @PostMapping("/api/Test/InventoryInfo")
    public ResponseDTO<Map<String, Object>> GetInventoryInfo(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myInventoryInfoService.findEquipment(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }
}
