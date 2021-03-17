package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class FieldDungeonRanking {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser; /**유저 아이디*/
    Long totalDamage;
    String teamCharactersIds; /** 해당유저의 어둠의 균열 팀덱 정보*/
    String teamCharacterCodes; /** 해당유저의 어둠의 균열 팀덱에 케릭터 코드*/
    String userGameName; /**유저 닉네임*/
    String profileHero;
    int profileFrame;
    int nowSeasonNo;

    public void refresh(String teamCharactersIds, String teamCharacterCodes, Long totalDamage, String profileHero, int profileFrame, int nowSeasonNo) {
        this.teamCharactersIds = teamCharactersIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.totalDamage += totalDamage;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
        this.nowSeasonNo = nowSeasonNo;
    }
}
