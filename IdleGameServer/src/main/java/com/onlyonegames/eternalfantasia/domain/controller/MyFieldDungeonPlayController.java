package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.FieldDungeonFinishRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyFieldDungeonPlayService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyFieldDungeonPlayController {
    private final MyFieldDungeonPlayService myFieldDungeonPlayService;

    @GetMapping("/api/DirectBuyFieldDungeonTicket")
    public ResponseDTO<Map<String, Object>> DirectBuyFieldDungeonTicket() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myFieldDungeonPlayService.DirectBuyFieldDungeonTicket(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/FieldDungeonPlay")
    public ResponseDTO<Map<String, Object>> FieldDungeonPlay() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myFieldDungeonPlayService.FieldDungeonPlay(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/FieldDungeonFinish")
    public ResponseDTO<Map<String, Object>> FieldDungeonFinish(@RequestBody FieldDungeonFinishRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myFieldDungeonPlayService.FieldDungeonFinish(userId, dto.getTotalDamage(), dto.getGrade(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
