package com.onlyonegames.eternalfantasia.domain.model.entity.Contents;

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
public class MyArenaPlayData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    Long matchedUserId;
    int playableCount;
    boolean resetAbleMatchingUser;
    LocalDateTime battleStartTime;
    LocalDateTime battleEndTime;

    public void SetMatchedUserId(Long matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public void ResetPlayableCount() {
        this.playableCount = 5;
    }

    public boolean SpendPlayableCount() {
        if(this.playableCount < 1)
            return false;
        playableCount -= 1;
        return true;
    }

    public void StartArenaPlay() {
        this.battleStartTime = LocalDateTime.now();
        this.resetAbleMatchingUser = true;
    }

    public void ClearArenaPlay() {
        this.battleEndTime = LocalDateTime.now();
    }

    public void DefeatArenaPlay() {
        this.battleEndTime = LocalDateTime.now();
    }
}
