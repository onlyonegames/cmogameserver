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
public class MyGachaInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    public Long id;
    public Long useridUser;
    public int weaponLevel;
    public int weaponExp;
    public int classLevel;
    public int classExp;
    public int weaponAD;
    public int classAD;
    public int artifactAD;
    public int runeLevel;
    public int runeExp;
    public int runeAD;

    public void ResetADCount() {
        this.weaponAD = 5;
        this.classAD = 5;
        this.artifactAD = 5;
        this.runeAD = 5;
    }
}
