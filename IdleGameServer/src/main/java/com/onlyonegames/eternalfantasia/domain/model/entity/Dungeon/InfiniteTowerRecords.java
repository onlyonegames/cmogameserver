package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * 천공의 계단 각 층별 최초 클리어 유저 등록
 **/
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class InfiniteTowerRecords extends BaseTimeEntity {
    @Id
    int id;
    int floor;
    Long useridUser;
    String teamCharactersIds; // 해당유저의  팀덱 정보
    String teamCharacterCodes; // 해당유저의 아레나 팀덱에 케릭터 코드
    String userGameName; //유저 닉네임
    String profileHero;
    int profileFrame;

    public void Record(Long useridUser, String teamCharactersIds, String teamCharacterCodes, String userGameName, String profileHero, int profileFrame){
        this.useridUser = useridUser;
        this.teamCharactersIds = teamCharactersIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.userGameName = userGameName;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
    }
}
