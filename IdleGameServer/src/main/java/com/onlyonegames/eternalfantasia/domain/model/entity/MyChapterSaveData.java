package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class MyChapterSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String saveDataValue;

    @Builder
    public MyChapterSaveData(Long useridUser, String saveDataValue) {
        this.useridUser = useridUser;
        this.saveDataValue = saveDataValue;
    }

    public void ResetSaveDataValue(String saveDataValue) {
        this.saveDataValue = saveDataValue;
    }
}
