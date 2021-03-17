package com.onlyonegames.eternalfantasia.domain.controller.managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.CreateEquipmentAssignmentDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo.EquipmentPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class EquipmentPaymentController {
    private final EquipmentPaymentService equipmentPaymentService;

    @PostMapping("/api/Test/EquipmentPayment")
    public ResponseDTO<Map<String, Object>> SendEquipment(@RequestBody CreateEquipmentAssignmentDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = equipmentPaymentService.createEquipmentAssignment(dto,map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
