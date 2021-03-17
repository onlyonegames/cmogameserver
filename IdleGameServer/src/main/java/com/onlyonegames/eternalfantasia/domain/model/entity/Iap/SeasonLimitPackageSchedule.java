package com.onlyonegames.eternalfantasia.domain.model.entity.Iap;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class SeasonLimitPackageSchedule extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String iapTableCode;
    String seasonName;
    LocalDateTime seasonStartTime;
    LocalDateTime seasonEndTime;
}
