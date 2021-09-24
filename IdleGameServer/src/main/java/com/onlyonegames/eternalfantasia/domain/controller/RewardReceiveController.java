package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PassAllRewardRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PassPurchaseRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.PassRewardRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.RewardReceiveService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class RewardReceiveController {
    private final RewardReceiveService rewardReceiveService;

    @PostMapping("/api/Pass/GetReward")
    public ResponseDTO<Map<String, Object>> GetReward(@RequestBody PassRewardRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = rewardReceiveService.GetReward(userId, dto.getRewardType(), dto.isPassReward(), dto.getLevelIndex(), dto.getIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Pass/GetAllReward")
    public ResponseDTO<Map<String, Object>> GetAllReward(@RequestBody PassAllRewardRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = rewardReceiveService.GetAllReward(userId, dto.getRewardType(), dto.getPassIndex(), dto.getLevelIndex(), dto.getIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Pass/Purchase")
    public ResponseDTO<Map<String, Object>> Purchase(@RequestBody PassPurchaseRequestDto dto) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = rewardReceiveService.Purchase(userId, dto.getPassType(), dto.getLevelIndex(), dto.getPayLoad(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Pass/TestPurchase")
    public ResponseDTO<Map<String, Object>> TestPurchase(@RequestBody PassPurchaseRequestDto dto) throws IOException {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = rewardReceiveService.PurchaseTest(userId, dto.getPassType(), dto.getLevelIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
