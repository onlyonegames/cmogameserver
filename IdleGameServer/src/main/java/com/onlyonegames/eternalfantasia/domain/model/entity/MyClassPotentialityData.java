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
public class MyClassPotentialityData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public String warrior;
    public String thief;
    public String knight;
    public String archer;
    public String magician;
    public Long markOfAttackCount;
    public Long markOfHealthCount;
    public Long markOfDamageUpCount;
    public Long markOfDamageDownCount;
    public Long markOfCriticalCount;

    public void ResetJson_warrior (String warrior){
        this.warrior = warrior;
    }

    public void ResetJson_thief (String thief){
        this.thief = thief;
    }

    public void ResetJson_knight (String knight){
        this.knight = knight;
    }

    public void ResetJson_archer (String archer){
        this.archer = archer;
    }

    public void ResetJson_magician (String magician){
        this.magician = magician;
    }
}
