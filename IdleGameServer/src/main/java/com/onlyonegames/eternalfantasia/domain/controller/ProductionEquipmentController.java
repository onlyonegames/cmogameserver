package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.*;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.HeroEquipmentProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ProductionEquipmentController {
    private final HeroEquipmentProductionService heroEquipmentProductionService;
    @Autowired
    public ProductionEquipmentController(HeroEquipmentProductionService heroEquipmentProductionService) {
        this.heroEquipmentProductionService = heroEquipmentProductionService;
    }

    @PostMapping("/api/OpenSlot")
    public ResponseDTO<Map<String, Object>> OpenSlot(@RequestBody ProductSlotOpenDto productSlotOpenDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.OpenSlot(userId, productSlotOpenDto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /*장비 재료 셋팅*/
    @PostMapping("/api/ProductionMaterialSet")
    public ResponseDTO<Map<String, Object>> ProductionMaterialSet(@RequestBody ProductionMaterialSetRequestDto productionMaterialSetRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.ProductionMaterialSet(userId, productionMaterialSetRequestDto.getItemCategory(), productionMaterialSetRequestDto.getMaterialIndex(), productionMaterialSetRequestDto.getSetCount(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /*장비 제작 시작*/
    @PostMapping("/api/ProductionEquipment")
    public ResponseDTO<Map<String, Object>> StartProductEquipment(@RequestBody StartProductRequestDto startProductRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.EquipmentProduction(userId, startProductRequestDto.getSlotNo(), startProductRequestDto.getItemCategory(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    /*장비 제작 슬롯 리스트 요청*/
    @PostMapping("/api/RequestEquipmentProductionList")
    public ResponseDTO<Map<String, Object>> RequestProductionList(@RequestBody UserBaseDto userBaseDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.RequestEquipmentProductionList(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*완료된 제작 장비 완료 요청*/
    @PostMapping("/api/ProductedEquipmentReceive")
    public ResponseDTO<Map<String, Object>> ProductedEquipmentReceive(@RequestBody ProductedReceiveEquipmentDto productedReceiveEquipmentDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.RequestProductedEquipment(userId, productedReceiveEquipmentDto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*제작 중인 장비 즉시완료 요청*/
    @PostMapping("/api/ProductedEquipmentImmediately")
    public ResponseDTO<Map<String, Object>> ProductedEquipmentImmediatly(@RequestBody ProductedEquipmentImmediatelyDto productedEquipmentImmediatelyDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.RequestProductedEquipmentImmediately(userId, productedEquipmentImmediatelyDto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*제작 중인 장비 광고로 시간단축 요청*/
    @PostMapping("/api/DecreaseProductTimeFromAD")
    public ResponseDTO<Map<String, Object>> RequestDecreaseProductTimeFromAD(@RequestBody DecreaseProductTimeFromADDto decreaseProductTimeFromADDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.DecreaseProductTimeFromAD(userId, decreaseProductTimeFromADDto.getSlotNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*숙련도에 의해 획득할 보상이 생겼을때 해당 보상 획득 요청*/
    @PostMapping("/api/RequestMasteryReward")
    public ResponseDTO<Map<String, Object>> RequestMasteryReward(@RequestBody MasteryRewardRequestDto masteryRewardRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentProductionService.RequestMasteryReward(userId, masteryRewardRequestDto.getReceiveIndex(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
    /*튜토리얼 장비 시작*/
//    @PostMapping("/api/TutorialProductionEquipment")
//    public ResponseDTO<Map<String, Object>> StartTutorialProductEquipment(@RequestBody StartProductRequestDto startProductRequestDto) {
//        Map<String, Object> map = new HashMap<>();
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Map<String, Object> response = heroEquipmentProductionService.TutorialEquipmentProduction(userId, map);
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }
    /*튜토리얼 장비 즉시완료 요청*/
//    @PostMapping("/api/TutorialProductedEquipmentImmediately")
//    public ResponseDTO<Map<String, Object>> TutorialProductedEquipmentImmediatly(@RequestBody ProductedEquipmentImmediatelyDto productedEquipmentImmediatelyDto) {
//        Map<String, Object> map = new HashMap<>();
//        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Map<String, Object> response = heroEquipmentProductionService.TutorialProductedEquipmentImmediately(userId, map);
//        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
//    }
}
