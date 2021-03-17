package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.GamepotResponse;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.CouponWebHookDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GamePot.IapWebHookDto;
import com.onlyonegames.eternalfantasia.domain.service.Coupon.CouponService;
import com.onlyonegames.eternalfantasia.domain.service.Iap.IapService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class GamepotConnectorController {
    private final IapService iapService;
    private final CouponService couponService;
    //https://{domain}?
    //userId={uuid}&orderId={orderId}&projectId={projectId}&platform={platform}&productId={productId}&store={store}&payment={payment}&transactionId={transactionId}&gamepotOrderId={gamepotOrderId}&uniqueId={uniqueId}
    @GetMapping("/api/Test/GamepotConnectorController")
    public GamepotResponse TestInappWebHook(@RequestParam HashMap<String, String> paramMap) {
        String userId = paramMap.get("userId");
        String transactionId = paramMap.get("transactionId");
        String store = paramMap.get("store");
        String projectId = paramMap.get("projectId");
        String productId = paramMap.get("productId");
        String platform = paramMap.get("platform");
        String payment = paramMap.get("payment");
        String uniqueId = paramMap.get("uniqueId");
        String gamepotOrderId = paramMap.get("gamepotOrderId");
        String serverId = paramMap.get("serverId");
        String playerId = paramMap.get("playerId");
        String etc = paramMap.get("etc");

        IapWebHookDto iapWebHookDto = new IapWebHookDto(userId, transactionId, store, projectId, productId, platform, payment, uniqueId, gamepotOrderId,
                serverId, playerId, etc);
        Map<String, Object> map = new HashMap<>();

        int status = iapService.IapWebHookFromGamepot(iapWebHookDto, map);
        String message = "";
        if(status < 1) {
            message = (String)map.get("message");
        }
        return new GamepotResponse(status, message);
    }

    @GetMapping("/api/GamePot/InappWebHook")
    public GamepotResponse InappWebHook(@RequestParam HashMap<String, String> paramMap) {
        String userId = paramMap.get("userId");
        String transactionId = paramMap.get("transactionId");
        String store = paramMap.get("store");
        String projectId = paramMap.get("projectId");
        String productId = paramMap.get("productId");
        String platform = paramMap.get("platform");
        String payment = paramMap.get("payment");
        String uniqueId = paramMap.get("uniqueId");
        String gamepotOrderId = paramMap.get("gamepotOrderId");
        String serverId = paramMap.get("serverId");
        String playerId = paramMap.get("playerId");
        String etc = paramMap.get("etc");

        IapWebHookDto iapWebHookDto = new IapWebHookDto(userId, transactionId, store, projectId, productId, platform, payment, uniqueId, gamepotOrderId,
                serverId, playerId, etc);
        Map<String, Object> map = new HashMap<>();

        int status = iapService.IapWebHookFromGamepot(iapWebHookDto, map);
        String message = "";
        if(status < 1) {
            message = (String)map.get("message");
        }
        return new GamepotResponse(status, message);
    }

    @GetMapping("/api/Test/CouponWebHook")
    public GamepotResponse TestCouponWebHook(@RequestParam HashMap<String, String> paramMap) {
        String userId = paramMap.get("userId");
        String projectId = paramMap.get("projectId");
        String platform = paramMap.get("platform");
        String store = paramMap.get("store");
        String title = paramMap.get("title");
        String content = paramMap.get("content");
        String userData = paramMap.get("userData");
        String itemId = paramMap.get("itemId");

        CouponWebHookDto cuponWebHookDto = new CouponWebHookDto(userId, projectId, platform, store, title, content, userData, itemId);
        Map<String, Object> map = new HashMap<>();

        int status = couponService.CuponWebHookFromGamepot(cuponWebHookDto, map);
        String message = "";
        if(status < 1) {
            message = (String)map.get("message");
        }
        return new GamepotResponse(status, message);
    }

    @GetMapping("/api/GamePot/CouponWebHook")
    public GamepotResponse CouponWebHook(@RequestParam HashMap<String, String> paramMap) {
        String userId = paramMap.get("userId");
        String projectId = paramMap.get("projectId");
        String platform = paramMap.get("platform");
        String store = paramMap.get("store");
        String title = paramMap.get("title");
        String content = paramMap.get("content");
        String userData = paramMap.get("userData");
        String itemId = paramMap.get("itemId");

        CouponWebHookDto cuponWebHookDto = new CouponWebHookDto(userId, projectId, platform, store, title, content, userData, itemId);
        Map<String, Object> map = new HashMap<>();

        int status = couponService.CuponWebHookFromGamepot(cuponWebHookDto, map);
        String message = "";
        if(status < 1) {
            message = (String)map.get("message");
        }
        return new GamepotResponse(status, message);
    }

}
