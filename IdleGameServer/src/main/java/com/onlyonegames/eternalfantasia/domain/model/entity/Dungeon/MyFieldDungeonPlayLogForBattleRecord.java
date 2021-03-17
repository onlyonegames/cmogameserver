package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyFieldDungeonPlayLogForBattleRecord extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    Long damage;
    boolean newLog;
    String profileHero;
    int profileFrame;
    LocalDateTime battleEndTime;

    public void CheckedLog(){
        this.newLog = false;
    }
}
