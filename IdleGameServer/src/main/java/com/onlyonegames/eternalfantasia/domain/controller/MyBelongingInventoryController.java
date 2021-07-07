package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ChangeUserGameNameRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.MyBelongingInventoryService;
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
public class MyBelongingInventoryController {
    private final MyBelongingInventoryService myBelongingInventoryService;

    @PostMapping("/api/User/ChangeGameName")
    public ResponseDTO<Map<String, Object>> ChangeUserGameName(@RequestBody ChangeUserGameNameRequestDto dto){
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myBelongingInventoryService.ChangeGameName(userId, dto.getName(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
