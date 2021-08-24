package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "serverstatusinfo")
public class ServerStatusInfo {
    @Id
    int id;
    int serverStatus;
}
