package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ArenaSeasonResetData {
    @Id
    int id;
    boolean resetting;

    public void StartArenaSeasonReset() {
        resetting = true;
    }

    public void EndArenaSeasonReset() {
        resetting = false;
    }
}
