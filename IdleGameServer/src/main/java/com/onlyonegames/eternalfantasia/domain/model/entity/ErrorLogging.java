package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorLogging extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    int errorCode;
    String errorDetail;
    String errorClass;
    String errorFunction;
    int lineNumber;

    @Builder
    public ErrorLogging(Long useridUser, int errorCode, String errorDetail, String errorClass, String errorFunction, int lineNumber) {
        this.useridUser = useridUser;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.errorClass = errorClass;
        this.errorFunction = errorFunction;
        this.lineNumber = lineNumber;
    }

}
