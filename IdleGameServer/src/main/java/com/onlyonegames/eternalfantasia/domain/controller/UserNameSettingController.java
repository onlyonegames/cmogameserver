package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/UserNameSetting"})
public class UserNameSettingController {
    private final UserService userService;

    public UserNameSettingController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseDTO<Map<String, Object>> UserNameSetting(@RequestBody UserBaseDto userBaseDto)
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = null;
        try {
            response = userService.setUserName(userId, userBaseDto.getUserGameName(), map);
        } catch (DataIntegrityViolationException ex) {
            throw new MyCustomException("Fail! -> Cause: Already Exist name", ResponseErrorCode.AREADY_EXIST_USERNAME);
        }
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
