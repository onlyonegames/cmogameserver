package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ShopBuyRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Shop.MyShopService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyShopController {
    private final MyShopService myShopService;

    @GetMapping("/api/Hello")
    public ResponseDTO<Map<String, Object>> GetHello() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.getMyShopData(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/GetMyShopDataInfo")
    public ResponseDTO<Map<String, Object>> GetMyShopDataInfo() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.getMyShopData(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecycleMainShop")
    public ResponseDTO<Map<String, Object>> RecycleMainShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.mainShopRecycle(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecycleArenaShop")
    public ResponseDTO<Map<String, Object>> RecycleArenaShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.arenaShopRecycle(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecycleAncientShop")
    public ResponseDTO<Map<String, Object>> RecycleAncientShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.ancientShopRecycle(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecycleCurrencyShop")
    public ResponseDTO<Map<String, Object>> RecycleCurrencyShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.currencyShopRecycle(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecyclePieceShop")
    public ResponseDTO<Map<String, Object>> RecyclePieceShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.pieceShopRecycle(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/RecycleDarkObeShop")
    public ResponseDTO<Map<String, Object>> RecycleDarkObeShop() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.darkObeShopRecycle(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/BuyMainShop")
    public ResponseDTO<Map<String, Object>> BuyMainShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.mainShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/BuyArenaShop")
    public ResponseDTO<Map<String, Object>> BuyArenaShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.arenaShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/BuyAncientShop")
    public ResponseDTO<Map<String, Object>> BuyAncientShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.ancientShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
    @PostMapping("/api/BuyCurrencyShop")
    public ResponseDTO<Map<String, Object>> BuyCurrencyShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.currencyShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/BuyPieceShop")
    public ResponseDTO<Map<String, Object>> BuyPieceShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.pieceShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/BuyDarkObeShop")
    public ResponseDTO<Map<String, Object>> BuyDarkObeShop(@RequestBody ShopBuyRequestDto shopBuyRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myShopService.darkObeShopBuy(userId, shopBuyRequestDto.getSlotIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

}
