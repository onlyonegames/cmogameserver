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
    public int challengeTowerFloor;
    public int adventureStage;
    public int adventureDifficulty;

    public void SetChallengeTowerFloor(int challengeTowerFloor) {
        this.challengeTowerFloor = challengeTowerFloor;
    }

    public void SetChallengeTowerFloor(String challengeTowerFloor) {
        this.challengeTowerFloor = Integer.parseInt(challengeTowerFloor);
    }

    public void ClearStage() { //마지막 스테이지를 확인하여 최대 제한
        if (adventureDifficulty == 5 && this.adventureStage == 0)
            return;
        if(this.adventureStage+1>=140){
            if(this.adventureDifficulty < 5)
                this.adventureDifficulty++;
            this.adventureStage = 0;
        }
        else
            this.adventureStage++;
    }

    public void ClearFloor() {
        this.challengeTowerFloor++;
    }
}