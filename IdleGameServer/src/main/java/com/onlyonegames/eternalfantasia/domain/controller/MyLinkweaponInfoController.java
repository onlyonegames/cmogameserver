package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LearnLinkforceRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LinkWeaponCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyLinkweaponInfoService;
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
public class MyLinkweaponInfoController {
    private final MyLinkweaponInfoService myLinkweaponInfoService;

    @Autowired
    public MyLinkweaponInfoController(MyLinkweaponInfoService myLinkweaponInfoService) {
        this.myLinkweaponInfoService = myLinkweaponInfoService;
    }

    @PostMapping("/api/LinkWeaponStrengthen")
    public ResponseDTO<Map<String, Object>> LinkWeaponStrengthen(@RequestBody LinkWeaponCommonRequestDto linkWeaponCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkweaponInfoService.Strengthen(userId, linkWeaponCommonRequestDto.getCharacterId(),linkWeaponCommonRequestDto.getWeaponId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
//
    @PostMapping("/api/LinkWeaponRevolution")
    public ResponseDTO<Map<String, Object>> LinkWeaponRevolution(@RequestBody LinkWeaponCommonRequestDto linkWeaponCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkweaponInfoService.Revolution(userId, linkWeaponCommonRequestDto.getCharacterId(),linkWeaponCommonRequestDto.getWeaponId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/LinkWeaponChangeOption")
    public ResponseDTO<Map<String, Object>> LinkWeaponChangeOption(@RequestBody LinkWeaponCommonRequestDto linkWeaponCommonRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkweaponInfoService.ChangeLinkweaponOption(userId, linkWeaponCommonRequestDto.getCharacterId(),linkWeaponCommonRequestDto.getWeaponId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
