package com.onlyonegames.eternalfantasia.domain.model.entity.Event;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyMonsterKillEventMissionData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String json_saveDataValue;
    Long useridUser;

    public void ResetSaveDataValue(String json_saveDataValue) { this.json_saveDataValue = json_saveDataValue; }
}
