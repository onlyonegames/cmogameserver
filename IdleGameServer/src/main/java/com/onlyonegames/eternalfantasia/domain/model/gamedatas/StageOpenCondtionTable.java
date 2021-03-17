package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "stageopencondtiontable")
public class StageOpenCondtionTable {
    @Id
    int stageopencondtiontable_id;
    String stage;
    String eventCode;
}
