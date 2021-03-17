package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.LinkAbilityLevelUpRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyLinkAbilityService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyLinkAbilityController {
    private final MyLinkAbilityService myLinkAbilityService;

    public MyLinkAbilityController(MyLinkAbilityService myLinkAbilityService) {
        this.myLinkAbilityService = myLinkAbilityService;
    }

    @PostMapping("/api/LinkAbilityLevelUp")
    public ResponseDTO<Map<String, Object>> LinkAbilityLevelUp(@RequestBody LinkAbilityLevelUpRequestDto linkAbilityLevelUpRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myLinkAbilityService.LinkAbilityLevelUp(userId, linkAbilityLevelUpRequestDto.getCharacterId(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
