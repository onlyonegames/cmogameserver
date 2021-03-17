package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LinkWeaponCommonRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
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
public class MyLinkweaponInfoTestController {
    private final MyLinkweaponInfoService myLinkweaponInfoService;

    @Autowired
    public MyLinkweaponInfoTestController(MyLinkweaponInfoService myLinkweaponInfoService) {
        this.myLinkweaponInfoService = myLinkweaponInfoService;
    }

    @PostMapping("/api/LinkWeaponClear")
    public ResponseDTO<Map<String, Object>> AllClear(@RequestBody UserBaseDto dto) {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> response = myLinkweaponInfoService.AllClear(dto.getId(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
