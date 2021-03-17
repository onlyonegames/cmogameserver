package com.onlyonegames.eternalfantasia.domain.model.entity.HotTimeEvent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@ToString
@Entity(name = "hotTimeFieldObjectInfo")
public class HotTimeFieldObjectInfo {
    @Id
    int id;
    int field;
    String gettingItem;
    /**클라이언트에서 활용하는 데이터*/
    float genCooltime;
    /**추가*/
    int maxGenCount;
    int onceGenCount;
    String type;
}
