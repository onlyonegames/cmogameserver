package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "chapteropenconditiontable")
public class ChapterOpenConditionTable {
    @Id
    int id;
    int chapterNo;
    String eventCode;
}
