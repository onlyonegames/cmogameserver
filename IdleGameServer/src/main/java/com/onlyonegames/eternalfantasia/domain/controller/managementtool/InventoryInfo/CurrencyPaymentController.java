package com.onlyonegames.eternalfantasia.domain.controller.managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.CurrencyDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo.CurrencyPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CurrencyPaymentController {
    private final CurrencyPaymentService currencyPaymentService;

    @PostMapping("/api/Test/CurrencyPayment")
    public ResponseDTO<Map<String, Object>> AddCurrency(@RequestBody CurrencyDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = currencyPaymentService.AddCurrency(dto, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true,response);
    }
}
