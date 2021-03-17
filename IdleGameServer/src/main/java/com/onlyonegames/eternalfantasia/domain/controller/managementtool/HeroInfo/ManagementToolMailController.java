package com.onlyonegames.eternalfantasia.domain.controller.managementtool.HeroInfo;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.AddUserIdDto;
import com.onlyonegames.eternalfantasia.domain.controller.managementtool.FindMailDto;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.UserInfo.ManagementToolMailService;
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
public class ManagementToolMailController {
    private final ManagementToolMailService managementToolMailService;

    @GetMapping("api/Test/ManagementToolGetMail")
    public ResponseDTO<Map<String, Object>> getAllMail(){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = managementToolMailService.GetAllMail(map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/DeleteMail")
    public ResponseDTO<Map<String, Object>> deleteMail(@RequestBody AddUserIdDto dto) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = managementToolMailService.DeleteMail(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/ManagementToolMails")
    public ResponseDTO<Map<String, Object>> findUserMail(@RequestBody AddUserIdDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = managementToolMailService.GetMailsByUserId(dto.getUserId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }

    @PostMapping("/api/Test/ManagementToolMail")
    public ResponseDTO<Map<String, Object>> findMail(@RequestBody FindMailDto dto){
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> response = managementToolMailService.GetMailByMailId(dto.getMailId(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(),"",true, response);
    }
}
