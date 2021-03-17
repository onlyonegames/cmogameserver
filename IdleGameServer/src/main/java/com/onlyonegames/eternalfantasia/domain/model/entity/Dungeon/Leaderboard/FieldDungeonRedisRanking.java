package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash("fielddungeonredisranking")
public class FieldDungeonRedisRanking implements Serializable {
    private static final long serialVersionUID = 9075014160304816158L;
    @Id
    Long id;/**유저 아이디*/
    Long totalDamage;
    String teamCharactersIds;  //해당 유저의 아레나 팀덱 정보
    String teamCharacterCodes; // 해당유저의 아레나 팀덱에 케릭터 코드
    String userGameName;
    String profileHero;
    int profileFrame;

    public void refresh(String teamCharacterIds, String teamCharacterCodes, long totalDamage, String profileHero, int profileFrame){
        this.totalDamage = totalDamage;
        this.teamCharactersIds = teamCharacterIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
    }
}
