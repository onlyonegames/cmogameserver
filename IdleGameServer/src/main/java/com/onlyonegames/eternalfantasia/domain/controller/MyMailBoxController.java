package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.MailSendRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ReadMailRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ReadOptionMailRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyMailBoxController {

    private final MyMailBoxService myMailBoxService;

    @GetMapping("/api/MyMailBox/GetMyMailBox")
    public ResponseDTO<Map<String, Object>> GetMyMailBox() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.GetMyMailBox(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/MyMailBox/ReadMail")
    public ResponseDTO<Map<String, Object>> ReadMail(@RequestBody ReadMailRequestDto readMailRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.ReadMail(userId, readMailRequestDto.getMailId(), map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @GetMapping("/api/MyMailBox/ReadMailAll")
    public ResponseDTO<Map<String, Object>> ReadMailAll() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.ReadMailAll(userId, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/MyMailBox/SendMail")
    public ResponseDTO<Map<String, Object>> SendMail(@RequestBody MailSendRequestDto mailSendRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myMailBoxService.SendMail(mailSendRequestDto, map);
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

    @PostMapping("/api/MyMailBox/ReadOptionMail")
    public ResponseDTO<Map<String, Object>> ReadOptionMail(@RequestBody ReadOptionMailRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.ReadOptionMail(userId, dto.getMailId(), dto.getSelectedCode(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
