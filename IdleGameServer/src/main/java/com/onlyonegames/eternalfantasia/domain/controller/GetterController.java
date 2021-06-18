package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.GetterRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.RequestDto;
import com.onlyonegames.eternalfantasia.domain.service.GetterService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class GetterController {
    private final GetterService getterService;

    @PostMapping("/api/Getter")
    public ResponseDTO<Map<String, Object>> Getter(@RequestBody RequestDto dto) throws IllegalAccessException, NoSuchFieldException {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RequestDto modifyData = new RequestDto();
        modifyData.cmds = new ArrayList<>();
        modifyData.cmds.addAll(dto.cmds);
        Map<String, Object> response = getterService.Getter(userId, modifyData, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
