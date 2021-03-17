package com.onlyonegames.eternalfantasia.domain.model.entity.EternalPass;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class EternalPasses extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Integer id;
    String seasonName;
    String seasonCharacterCode;
    LocalDateTime passStartTime;
    LocalDateTime passEndTime;
}
