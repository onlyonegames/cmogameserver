package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.PreviousFieldDungeonSeasonRanking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreviousFieldDungeonSeasonRankingDto {
    Long id;
    Long useridUser; /**유저 아이디*/
    int seasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    Long score;             /**점수*/
    Long ranking; /**랭킹*/

    public PreviousFieldDungeonSeasonRanking ToEntity() {
        return PreviousFieldDungeonSeasonRanking.builder().useridUser(useridUser).seasonInfoId(seasonInfoId).score(score).ranking(ranking).build();
    }
}
