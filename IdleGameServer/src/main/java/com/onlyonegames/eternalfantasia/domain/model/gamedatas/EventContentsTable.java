package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import com.onlyonegames.eternalfantasia.domain.model.EventTypeName;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Data
@Entity(name = "eventcontentstable")
public class EventContentsTable {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    private int id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(length = 60)
    private EventTypeName code ;
    private String name;
    private String prefabName;
}
