package com.onlyonegames.eternalfantasia.domain;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler({ MyCustomException.class })
    public ResponseEntity<Object> handleMyCustomException(MyCustomException ex, WebRequest request) {

        log.error("Message: {} -> stackTrace: {}", ex.getMessage(), ex.getStackTrace());
        ResponseDTO<String> responseDTO = new ResponseDTO<>(HttpStatus.BAD_REQUEST,
                ex.GetResponseErrorCode().getIntegerValue(), ex.getMessage(), false, "");

        return new ResponseEntity<Object>(responseDTO, new HttpHeaders(), responseDTO.getStatus());
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        log.error("Message: {} -> stackTrace: {}", ex.getMessage(),ex.getStackTrace());

                //ExceptionUtils.getStackTrace(ex.fillInStackTrace()));
        ResponseDTO<String> responseDTO = new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR,
                ResponseErrorCode.UNDEFINED.getIntegerValue(), ex.getCause().getMessage(), false, "");

        return new ResponseEntity<Object>(responseDTO, new HttpHeaders(), responseDTO.getStatus());
    }
}