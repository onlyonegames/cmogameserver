package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyContentsInfo;
import lombok.Data;

@Data
public class MyContentsInfoDto {
    Long id;
    Long useridUser;
    int warriorChallengeTowerFloor;
    int thiefChallengeTowerFloor;
    int knightChallengeTowerFloor;
    int archerChallengeTowerFloor;
    int magicianChallengeTowerFloor;
    int adventureStage;
    int adventureDifficulty;
    int challengeTowerDiamondCount;

    public MyContentsInfo ToEntity(){
        Init();
        return MyContentsInfo.builder().useridUser(useridUser).warriorChallengeTowerFloor(warriorChallengeTowerFloor)
                .thiefChallengeTowerFloor(thiefChallengeTowerFloor).knightChallengeTowerFloor(knightChallengeTowerFloor)
                .archerChallengeTowerFloor(archerChallengeTowerFloor).magicianChallengeTowerFloor(magicianChallengeTowerFloor)
                .challengeTowerDiamondCount(challengeTowerDiamondCount).build();
    }

    public void Init() {
        this.warriorChallengeTowerFloor = 0;
        this.thiefChallengeTowerFloor = 0;
        this.knightChallengeTowerFloor = 0;
        this.archerChallengeTowerFloor = 0;
        this.magicianChallengeTowerFloor = 0;
        this.adventureStage = 0;
        this.adventureDifficulty = 0;
        this.challengeTowerDiamondCount = 0;
    }
}
