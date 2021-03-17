package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CompleteCouponTransactionRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Coupon.CouponService;
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
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/api/CompleteIapTransaction")
    public ResponseDTO<Map<String, Object>> CompleteIapTransaction(@RequestBody CompleteCouponTransactionRequestDto completeCouponTransactionRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String cuponNo = completeCouponTransactionRequestDto.getCouponNo();
        Map<String, Object> response = couponService.CompleteCouponTransaction(userId, cuponNo, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
