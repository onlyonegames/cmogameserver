package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "attendancerewardtable")
public class AttendanceRewardTable {
    @Id
    int id;
    String gettingItem;
    int gettingCount;
}
