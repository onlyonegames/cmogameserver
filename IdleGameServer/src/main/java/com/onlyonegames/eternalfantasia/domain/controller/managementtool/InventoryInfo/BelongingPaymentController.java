package com.onlyonegames.eternalfantasia.domain.controller.managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.BelongingPaymentDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo.BelongingPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class BelongingPaymentController {
    private final BelongingPaymentService belongingPaymentService;

    @PostMapping("/api/Test/BelongingPayment")
    public ResponseDTO<Map<String, Object>> BelongingPayment(@RequestBody BelongingPaymentDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = belongingPaymentService.addBelonging(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }
}
