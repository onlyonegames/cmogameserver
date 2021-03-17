package com.onlyonegames.eternalfantasia.domain;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 응답 도메인
 * 
 * @author 정경진
 * @version 2019.03.26 v1.0
 */
@AllArgsConstructor
@Data
public class ResponseDTO<T> {
    // 상태코드
    private HttpStatus status = HttpStatus.OK;
    // 실패시 에러코드
    private int errorCode = ResponseErrorCode.NONE.getIntegerValue();
    // 메세지
    private String message;
    // API 성공여부
    private boolean check = true;
    // Json데이터
    private T response;
}