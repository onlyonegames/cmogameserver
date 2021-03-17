package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "initJsonDatasForFirstUser")
public class InitJsonDatasForFirstUser {
    @Id
    int id;
    String description;
    String initJson;
}
