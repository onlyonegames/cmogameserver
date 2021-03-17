package com.onlyonegames.eternalfantasia.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {

        log.error("Unauthorized error. Message - {}", e.getMessage());
        String expiredMsg = (String) request.getAttribute("expired");
        String msg = (expiredMsg != null) ? expiredMsg : "Unauthorized";
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
    }
}