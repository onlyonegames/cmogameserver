package com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash("worldbossredisranking")
public class WorldBossRedisRanking implements Serializable {
    private static final long serialVersionUID = -5541987960439117840L;
    @Id
    Long id; //유저 아이디
    String userGameName;
    Long totalDamage;

    public void refresh(Long totalDamage) {
        this.totalDamage = totalDamage;
    }
}
