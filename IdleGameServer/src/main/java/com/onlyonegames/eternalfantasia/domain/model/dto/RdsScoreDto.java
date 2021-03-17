package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.RdsScore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RdsScoreDto implements Serializable {
    Long id;
    Long useridUser; //유저 아이디
    int arenaSeasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    int rankingtiertableId; //점수별 등급
    Long score;
    String teamCharactersIds; // 해당유저의 아레나 팀덱 정보
    String teamCharacterCodes; // 해당유저의 아레나 팀덱에 케릭터 코드
    String userGameName; //유저 닉네임

    public void InitFromDbData(RdsScore dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.rankingtiertableId = dbData.getRankingtiertableId();
        this.score = dbData.getScore();
        this.teamCharactersIds = dbData.getTeamCharactersIds();
        this.teamCharacterCodes = dbData.getTeamCharacterCodes();
        this.userGameName = dbData.getUserGameName();
    }
}
