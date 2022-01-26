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
public class MyContentsInfo extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public int warriorChallengeTowerFloor;
    public int thiefChallengeTowerFloor;
    public int knightChallengeTowerFloor;
    public int archerChallengeTowerFloor;
    public int magicianChallengeTowerFloor;
    public int adventureStage;
    public int adventureDifficulty;
    public int challengeTowerDiamondCount;


    public void SetWarriorChallengeTowerFloor(int warriorChallengeTowerFloor) {
        this.warriorChallengeTowerFloor = warriorChallengeTowerFloor;
    }

    public void SetThiefChallengeTowerFloor(int thiefChallengeTowerFloor) {
        this.thiefChallengeTowerFloor = thiefChallengeTowerFloor;
    }

    public void SetKnightChallengeTowerFloor(int knightChallengeTowerFloor) {
        this.knightChallengeTowerFloor = knightChallengeTowerFloor;
    }

    public void SetArcherChallengeTowerFloor(int archerChallengeTowerFloor) {
        this.archerChallengeTowerFloor = archerChallengeTowerFloor;
    }
    public void SetMagicianChallengeTowerFloor(int magicianChallengeTowerFloor) {
        this.magicianChallengeTowerFloor = magicianChallengeTowerFloor;
    }


    public void ClearStage() { //마지막 스테이지를 확인하여 최대 제한
        if (adventureDifficulty == 7 && this.adventureStage == 0)
            return;
        if(this.adventureStage+1>=140){
            if(this.adventureDifficulty < 7)
                this.adventureDifficulty++;
            this.adventureStage = 0;
        }
        else
            this.adventureStage++;
    }

    public void DiamondEntry() {
        this.challengeTowerDiamondCount++;
    }

    public void ResetChallengeTowerDiamondCount() {
        this.challengeTowerDiamondCount = 0;
    }
}