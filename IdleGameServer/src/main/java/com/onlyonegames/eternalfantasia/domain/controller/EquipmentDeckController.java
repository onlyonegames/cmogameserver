package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EquipmentDeckCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.EquipmentDeckPassiveItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.StartProductRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyEquipmentDeckSetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EquipmentDeckController {
    private final MyEquipmentDeckSetterService myEquipmentDeckSetterService;
    @Autowired
    public EquipmentDeckController(MyEquipmentDeckSetterService myEquipmentDeckSetterService) {
        this.myEquipmentDeckSetterService = myEquipmentDeckSetterService;
    }

    @PostMapping("/api/EquipEquipment")
    public ResponseDTO<Map<String, Object>> EquipEquipment(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.Equip(userId, equipmentDeckCommonRequestDto.getItemInventoryId(), equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/UnEquipEquipment")
    public ResponseDTO<Map<String, Object>> UnEquipEquipment(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.UnEquip(userId, equipmentDeckCommonRequestDto.getItemInventoryId(), equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/PassiveItemEquip")
    public ResponseDTO<Map<String, Object>> PassiveItemEquip(@RequestBody EquipmentDeckPassiveItemRequestDto equipmentDeckPassiveItemRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.PassiveItemEquip(userId, equipmentDeckPassiveItemRequestDto.getItemInventoryId(), equipmentDeckPassiveItemRequestDto.getDeckNo(), equipmentDeckPassiveItemRequestDto.passiveSlotIndex, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/PassiveItemUnEquip")
    public ResponseDTO<Map<String, Object>> PassiveItemUnEquip(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.PassiveItemUnEquip(userId, equipmentDeckCommonRequestDto.getItemInventoryId(), equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AutoEquipEquipment")
    public ResponseDTO<Map<String, Object>> AutoEquipEquipment(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.AutoEquip(userId, equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/AllUnEquipEquipment")
    public ResponseDTO<Map<String, Object>> AllUnEquipEquipment(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.AllUnEquip(userId, equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/ChangeCurrentDeckNo")
    public ResponseDTO<Map<String, Object>> ChangeCurrentDeckNo(@RequestBody EquipmentDeckCommonRequestDto equipmentDeckCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myEquipmentDeckSetterService.ChangeCurrentDeckNo(userId, equipmentDeckCommonRequestDto.getDeckNo(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

}
