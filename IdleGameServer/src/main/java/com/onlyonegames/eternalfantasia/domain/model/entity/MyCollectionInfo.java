package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyCollectionInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    public String  json_weaponCollectionInfo;
    public String json_classCollectionInfo;
    public String json_monsterCollectionInfo;
    Long useridUser;

    public void ResetJson_weaponCollectionInfo (String json_weaponCollectionInfo){
        this.json_weaponCollectionInfo = json_weaponCollectionInfo;
    }
    public void ResetJson_classCollectionInfo (String json_classCollectionInfo){
        this.json_classCollectionInfo = json_classCollectionInfo;
    }
    public void ResetJson_monsterCollectionInfo (String json_monsterCollectionInfo){
        this.json_monsterCollectionInfo = json_monsterCollectionInfo;
    }
}
