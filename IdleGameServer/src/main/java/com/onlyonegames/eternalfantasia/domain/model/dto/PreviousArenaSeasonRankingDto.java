package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousArenaSeasonRanking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviousArenaSeasonRankingDto {
    Long id;
    Long useridUser; /**유저 아이디*/
    int arenaSeasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    int rankingtiertableId; /**점수별 등급*/
    Long score;             /**점수*/
    Long ranking;           /**랭킹*/

    public PreviousArenaSeasonRanking ToEntity() {
        return PreviousArenaSeasonRanking.builder().useridUser(useridUser)
                .arenaSeasonInfoId(arenaSeasonInfoId).rankingtiertableId(rankingtiertableId).score(score).ranking(ranking).build();
    }

}
