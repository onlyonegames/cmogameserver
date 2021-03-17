package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import lombok.Builder;
import lombok.Data;

@Data
public class RankingInfoDto {
    Long useridUser;
    String userGameName;
    String teamCharacterCodes; // 해당유저의 아레나 팀덱에 케릭터 코드
    Long score;
    int ranking;
    int rankingtiertableId; //점수별 등급
    String profileHero;
    int profileFrame;

    @Builder
    public RankingInfoDto(Long useridUser, String userGameName, String teamCharacterCodes, Long score, int ranking, int rankingtiertableId, String profileHero, int profileFrame) {
        this.useridUser = useridUser;
        this.userGameName = userGameName;
        this.teamCharacterCodes = teamCharacterCodes;
        this.score = score;
        this.ranking = ranking;
        this.rankingtiertableId = rankingtiertableId;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
    }

}
