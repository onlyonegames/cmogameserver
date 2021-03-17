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
public class InfiniteRanking extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser; /**유저 아이디*/
    int floor;
    String teamCharactersIds; /** 해당유저의 아레나 팀덱 정보*/
    String teamCharacterCodes; /** 해당유저의 아레나 팀덱에 케릭터 코드*/
    String userGameName; /**유저 닉네임*/
    String profileHero;
    int profileFrame;

    public void refresh(String teamCharactersIds, String teamCharacterCodes, int floor, String profileHero, int profileFrame) {
        this.teamCharactersIds = teamCharactersIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.floor = floor;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
    }
}
