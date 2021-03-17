package com.onlyonegames.eternalfantasia.domain.controller.managementtool;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.service.Managementtool.ManagementToolLoginService;
import com.onlyonegames.eternalfantasia.domain.service.UserService;
import com.onlyonegames.eternalfantasia.security.jwt.JwtProvider;
import com.onlyonegames.eternalfantasia.security.services.UserDetailsServiceImpl;
import com.onlyonegames.eternalfantasia.security.services.UserPrinciple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth/Tool/Login")
public class ToolLoginController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private ManagementToolLoginService managementToolLoginService;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    PasswordEncoder encoder;

    @PostMapping
    public ResponseDTO<Map<String, Object>> login(@RequestBody UserBaseDto userBaseDto) {
        // TODO: process POST request

        String socialid = userBaseDto.getSocialId();
        UserDetails userDetails = null;
        userDetails = userDetailsService.loadUserByUsername(socialid);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 생성
        String jwt = jwtProvider.generateJwtToken(authentication);
        Map<String, Object> map = new HashMap<>();
        map.put("jwt", jwt);

        // 로그인
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        User user = userPrincipal.getUser();

        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, managementToolLoginService.ManagementToolLogin(user.getId(), map));
    }
}
