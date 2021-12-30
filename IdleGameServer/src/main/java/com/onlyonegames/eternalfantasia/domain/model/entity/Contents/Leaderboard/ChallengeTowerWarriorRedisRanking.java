package com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash("warriorchallengetowerredisranking")
public class ChallengeTowerWarriorRedisRanking implements Serializable {
    private static final long serialVersionUID = -573230698926007228L;
    @Id
    Long id; //유저 아이디
    String userGameName;
    int point;

    public void refresh(int point) {
        this.point = point;
    }

    public void ResetUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }
}
