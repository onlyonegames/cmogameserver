package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "arenarewardstable")
public class ArenaRewardsTable {
    @Id
    int arenarewardstable_id;
    String rankGrade;
    String season;
    String league;
    String daily;
    String playCountReward;
    String playWinReward;
    String playDefeatReward;
    String hallofHonorReward;
}
