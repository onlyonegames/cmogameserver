package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "infinitetowerrewardstable")
public class InfiniteTowerRewardsTable {
    @Id
    int id;
    int floor;
    String reward;
}
