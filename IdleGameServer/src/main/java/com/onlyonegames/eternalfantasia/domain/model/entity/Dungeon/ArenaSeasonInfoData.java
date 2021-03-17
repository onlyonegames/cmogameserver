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
public class ArenaSeasonInfoData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    int id;
    int nowSeasonNo;
    String seasonName;
    LocalDateTime seasonStartTime;
    LocalDateTime seasonEndTime;

    public void SetSeasonEndTime(LocalDateTime setTime){
        this.seasonEndTime = setTime;
    }
}
