package com.onlyonegames.eternalfantasia.domain.model.entity.Event;

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
public class MyAchieveEventMissionsData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_saveDataValue;

    public void ResetSaveDataValue(String json_saveDataValue) { this.json_saveDataValue = json_saveDataValue;}
}
