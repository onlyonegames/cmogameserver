package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.CouponUseRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.CouponService;
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

    @PostMapping("/api/Coupon")
    public ResponseDTO<Map<String, Object>> UseCoupon(@RequestBody CouponUseRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = couponService.UseCoupon(userId, dto.getCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/Coupon")
    public ResponseDTO<Map<String, Object>> TestUseCoupon(@RequestBody CouponUseRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = 7302L;//(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = couponService.UseCoupon(userId, dto.getCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
