package com.onlyonegames.eternalfantasia.domain.controller.Mail;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ReadMailAllRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ReadMailRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MailExpireService;
import com.onlyonegames.eternalfantasia.domain.service.Mail.MyMailBoxService;
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
public class MailController {
    private final MailExpireService mailExpireService;
    private final MyMailBoxService myMailBoxService;

    @GetMapping("/api/Test/MailExpire")
    public ResponseDTO<Map<String, Object>> MailExpire() {
        Map<String, Object> map = new HashMap<>();
        map = mailExpireService.ExpireMail(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, map);
    }

    @GetMapping("/api/Mail/GetMyMailBox")
    public ResponseDTO<Map<String, Object>> GetMyMailBox() {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.GetMyMailBox(userId, map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Mail/ReadMail")
    public ResponseDTO<Map<String, Object>> ReadMail(@RequestBody ReadMailRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.ReadMail(userId, dto.getMailId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @PostMapping("/api/Mail/ReadMailAll")
    public ResponseDTO<Map<String, Object>> ReadMailAll(@RequestBody ReadMailAllRequestDto dto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = myMailBoxService.ReadMailAll(userId, dto.getMailType(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/Mail/PushMail12")
    public ResponseDTO<Map<String, Object>> PushMail12() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myMailBoxService.DailySendMail12(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }

    @GetMapping("/api/Test/Mail/PushMail6")
    public ResponseDTO<Map<String, Object>> PushMail6() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = myMailBoxService.DailySendMail6(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
