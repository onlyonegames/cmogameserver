package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.SetNoticeRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/api/Notice/GetContents")
    public ResponseDTO<Map<String, Object>> GetContents() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = noticeService.GetNotice(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Test/Notice/SetNotice")
    public ResponseDTO<Map<String, Object>> SetNotice(@RequestBody SetNoticeRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = noticeService.SetNotice(dto.getContents(), dto.getExpireDate(), dto.isForce(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
