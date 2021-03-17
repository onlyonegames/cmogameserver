package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "arenarankingrewardtable")
public class ArenaRankingRewardTable {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    int id;
    String rankingName;
    String ranking;
    String reward;
}
