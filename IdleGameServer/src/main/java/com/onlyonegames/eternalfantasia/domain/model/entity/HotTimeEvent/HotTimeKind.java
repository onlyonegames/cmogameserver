package com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "hotTimeKind")
public class HotTimeKind {
    @Id
    int id;
    /**int?*/String kind;
    String title;
}
