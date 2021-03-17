package com.onlyonegames.eternalfantasia.domain;

public class MyCustomException extends RuntimeException {
    private static final long serialVersionUID = -360920998230860820L;

    private ResponseErrorCode responseErrorCode;

    public MyCustomException(String message, ResponseErrorCode responseErrorCode) {
        super(message);
        this.responseErrorCode = responseErrorCode;
    }

    public ResponseErrorCode GetResponseErrorCode() {
        return responseErrorCode;
    }
}