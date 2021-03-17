package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.ErrorLogging;
import lombok.Data;

@Data
public class ErrorLoggingDto {
    Long id;
    Long useridUser;
    int errorCode;
    String errorDetail;
    String errorClass;
    String errorFunction;
    int lineNumber;

    public void setErrorLoggingDto(Long userid, int errorCode, String errorDetail, String errorClass, String errorFunction) {
        this.useridUser = userid;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.errorClass = errorClass;
        this.errorFunction = errorFunction;
    }

    public void setErrorLoggingDto(Long userid, int errorCode, String errorDetail, String errorClass, int lineNumber, String errorFunction) {
        this.useridUser = userid;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.errorClass = errorClass;
        this.errorFunction = errorFunction;
        this.lineNumber = lineNumber;
    }

    public ErrorLogging ToEntity() {
        return ErrorLogging.builder().useridUser(useridUser).errorCode(errorCode).errorDetail(errorDetail)
                .errorClass(errorClass).errorFunction(errorFunction).lineNumber(lineNumber).build();
    }
}
