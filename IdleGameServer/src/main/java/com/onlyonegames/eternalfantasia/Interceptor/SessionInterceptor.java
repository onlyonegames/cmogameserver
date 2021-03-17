package com.onlyonegames.eternalfantasia.Interceptor;

import com.google.common.base.Strings;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.eternalfantasia.security.jwt.JwtProvider;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Transactional
public class SessionInterceptor extends HandlerInterceptorAdapter {
    //세션 redis
    @Autowired
    private OnlyoneSessionRepository sessionRepository;
    @Autowired
    private JwtProvider tokenProvider;
    @Autowired
    private ErrorLoggingService errorLoggingService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = getJwt(request);
        if(!Strings.isNullOrEmpty(jwt)) {
            Long userid = tokenProvider.getUseridFromJwtToken(jwt);
            OnlyoneSession onlyoneSession = sessionRepository.findById(userid).orElse(null);

            if(onlyoneSession == null) {
                onlyoneSession = new OnlyoneSession(userid, jwt);
               // errorLoggingService.SetErrorLog(userid, ResponseErrorCode.EMPTY_SESSION.getIntegerValue(), "Fail! -> Cause: EmptySession.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
               // throw new MyCustomException("Fail! -> Cause: EmptySession.", ResponseErrorCode.EMPTY_SESSION);
            }
            if(!onlyoneSession.getJwt().equals(jwt)){
                errorLoggingService.SetErrorLog(userid, ResponseErrorCode.DUPLICATED_SESSION.getIntegerValue(), "Fail! -> Cause: Dubplicated Session.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Dubplicated Session.", ResponseErrorCode.DUPLICATED_SESSION);
            }
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(onlyoneSession.getSessionExpireTime())) {
                errorLoggingService.SetErrorLog(userid, ResponseErrorCode.EXPIRED_SESSION.getIntegerValue(), "Fail! -> Cause: Expired Session.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
                throw new MyCustomException("Fail! -> Cause: Expired Session.", ResponseErrorCode.EXPIRED_SESSION);
            }

            onlyoneSession.RenewalExpireTime();
            sessionRepository.save(onlyoneSession);
        }

        return true;
    }
    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }

}
