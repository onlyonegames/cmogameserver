package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "mainquestinfotable")
public class MainQuestInfoTable {
    @Id
    int index;
    String code;
    String questName;
    String discription;
    String startMethod;
    String eventCategory;
    String activeScene;
    String afterEvent;
}
