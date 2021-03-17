package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyProfileData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    String json_saveDataValue;
    String json_missionData;
    Long useridUser;

    public void ResetJson_saveDataValue(String json_saveDataValue) { this.json_saveDataValue = json_saveDataValue; }
    public void ResetJson_missionData(String json_missionData) { this.json_missionData = json_missionData; }
}
