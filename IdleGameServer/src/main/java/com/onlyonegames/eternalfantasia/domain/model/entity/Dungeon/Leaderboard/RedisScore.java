package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash("redisscore")
public class RedisScore implements Serializable {
    private static final long serialVersionUID = -601533462702743675L;

    @Id
    Long id;/**유저 아이디*/
    Long score;
    int arenaSeasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    int rankingtiertableId; /**점수별 등급*/
    String teamCharactersIds;  //해당 유저의 아레나 팀덱 정보
    String teamCharacterCodes; // 해당유저의 아레나 팀덱에 케릭터 코드
    String userGameName;
    String profileHero;
    int profileFrame;

    public void refresh(String teamCharactersIds, String teamCharacterCodes, long score, int rankingtiertable_id, int arenaSeasonInfoId, String profileHero, int profileFrame){
        this.score = score;
        this.teamCharactersIds = teamCharactersIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.rankingtiertableId = rankingtiertable_id;
        this.arenaSeasonInfoId = arenaSeasonInfoId;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
    }
}
