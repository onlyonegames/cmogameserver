package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CompleteIapTransactionRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.StartIapTransactionRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Iap.IapService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class IapController {
    private final IapService iapService;


    @PostMapping("/api/Iap/StartIapTransaction")
    public ResponseDTO<Map<String, Object>> StartIapTransaction(@RequestBody StartIapTransactionRequestDto startIapTransactionRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String productId = startIapTransactionRequestDto.getProductId();
        Map<String, Object> response = iapService.StartIapTransaction(userId, productId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    @PostMapping("/api/Iap/CompleteIapTransaction")
    public ResponseDTO<Map<String, Object>> CompleteIapTransaction(@RequestBody CompleteIapTransactionRequestDto completeIapTransactionRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long uniqueId = completeIapTransactionRequestDto.getUniqueId();
        Map<String, Object> response = iapService.CompleteIapTransaction(userId, uniqueId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

}
