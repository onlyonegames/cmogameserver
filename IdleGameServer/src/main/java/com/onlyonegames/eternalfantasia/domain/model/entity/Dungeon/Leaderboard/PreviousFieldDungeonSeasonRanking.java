package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class PreviousFieldDungeonSeasonRanking extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser; /**유저 아이디*/
    int seasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    Long score;             /**점수*/
    Long ranking;           /**랭킹*/
}
