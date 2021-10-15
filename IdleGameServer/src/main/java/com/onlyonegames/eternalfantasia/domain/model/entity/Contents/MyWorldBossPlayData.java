package com.onlyonegames.eternalfantasia.domain.model.entity.Contents;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyWorldBossPlayData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    int playableCount;
    Long playLogId;

    public void ResetPlayableCount() {
        this.playableCount = 3;
    }

    public boolean SpendPlayableCount() {
        if(this.playableCount < 1)
            return false;
        playableCount -= 1;
        return true;
    }

    public void SetPlayLogId(Long playLogId) {
        this.playLogId = playLogId;
    }
}
