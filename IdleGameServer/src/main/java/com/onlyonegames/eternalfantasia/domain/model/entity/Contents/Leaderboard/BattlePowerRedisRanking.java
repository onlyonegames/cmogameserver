package com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash("battlepowerredisranking")
public class BattlePowerRedisRanking implements Serializable {
    private static final long serialVersionUID = -5610996611176272198L;
    @Id
    Long id; //유저 아이디
    String userGameName;
    Long battlePower;

    public void refresh(Long battlePower) {
        this.battlePower = battlePower;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
