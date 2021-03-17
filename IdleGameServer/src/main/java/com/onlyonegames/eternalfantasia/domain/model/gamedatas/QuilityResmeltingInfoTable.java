package com.onlyonegames.eternalfantasia.domain.model.gamedatas;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "quilityresmeltinginfotable")
public class QuilityResmeltingInfoTable {
    @Id
    int id;
    String grade;
    String classStr;
    String needResmeltStone;
}
