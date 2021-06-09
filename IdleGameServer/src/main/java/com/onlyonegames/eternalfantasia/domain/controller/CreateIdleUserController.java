package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.service.CreateUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class CreateIdleUserController {
    private final CreateUserService createUserService;
    private final PasswordEncoder encoder;

    @PostMapping("/api/User")
    public ResponseDTO<Map<String, Object>> createUser(@RequestBody UserBaseDto entity) {
        // TODO: process POST request

        Map<String, Object> map = new HashMap<>();
        entity.setPassword(encoder.encode(entity.getPassword()));

        return new ResponseDTO<>(HttpStatus.OK,
                ResponseErrorCode.NONE.getIntegerValue(), "", true, createUserService.createUser(entity, map));
    }
}
