package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "rankingtiertable")
public class RankingTierTable {
    @Id
    int rankingtiertable_id;
    String rankGrade;
    String gradeName;
    int maxPoint;
    int totalPoint;
    String pointScope;
}
