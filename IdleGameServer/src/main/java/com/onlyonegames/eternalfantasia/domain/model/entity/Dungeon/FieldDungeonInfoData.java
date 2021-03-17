package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class FieldDungeonInfoData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    int id;
    LocalDateTime dungeonStartTime;
    LocalDateTime dungeonEndTime;
    int nowSeasonNo;
    String bossCode;
    boolean seasonEnd;

    public void SeasonEnd() {
        this.seasonEnd = true;
    }

    public void SetEndTime(LocalDateTime setTime) {
        this.dungeonEndTime = setTime;
    }
}
