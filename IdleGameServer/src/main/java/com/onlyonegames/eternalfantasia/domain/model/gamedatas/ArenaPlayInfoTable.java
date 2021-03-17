package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "arenaplayinfotable")
public class ArenaPlayInfoTable {
    @Id
    int arenaplayinfotable_id;
    int needCountArenaTicket;
    int maxPlayPerDayCount;/*반복대전보상을 받을수 있는 최대 반복대전 카운트*/
    int remachingNeedDiamond;
}
