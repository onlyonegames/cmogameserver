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
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    Long matchedUserId;
    int playableCount;
    int reMatchingAbleCount;
    boolean resetAbleMatchingUser;
    Long arenaPlayLogId;

    public void SetMatchedUserId(Long matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public void ResetPlayableCount() {
        this.playableCount = 5;
    }

    public void ResetReMatchingAbleCount() {
        this.reMatchingAbleCount = 3;
    }

    public void ResetResetAbleMatchingUser() {
        this.resetAbleMatchingUser = true;
    }

    public void ResetUnResetAbleMatchingUser() {
        this.resetAbleMatchingUser = false;
    }

    public boolean SpendPlayableCount() {
        if(this.playableCount < 1)
            return false;
        playableCount -= 1;
        return true;
    }

    public boolean SpendReMatchingAbleCount() {
        if(this.reMatchingAbleCount < 1)
            return false;
        reMatchingAbleCount -= 1;
        return true;
    }

    public void SetArenaPlayLogId(Long arenaPlayLogId) {
        this.arenaPlayLogId = arenaPlayLogId;
    }
}
