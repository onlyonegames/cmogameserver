package com.onlyonegames.eternalfantasia.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public abstract class CommonLogEntity extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    int logType;
    String json_LogDetail;

    public CommonLogEntity(Long useridUser, int logType, String json_LogDetail) {
        this.useridUser = useridUser;
        this.logType = logType;
        this.json_LogDetail = json_LogDetail;
    }
}
