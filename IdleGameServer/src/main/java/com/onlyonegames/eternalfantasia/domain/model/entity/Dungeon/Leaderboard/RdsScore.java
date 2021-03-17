package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard;


import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class RdsScore extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser; /**유저 아이디*/
    Long score;
    int arenaSeasonInfoId;  /**마지막으로 갱신한 정보의 시즌 번호*/
    int rankingtiertableId; /**점수별 등급*/
    String teamCharactersIds; /** 해당유저의 아레나 팀덱 정보*/
    String teamCharacterCodes; /** 해당유저의 아레나 팀덱에 케릭터 코드*/
    String userGameName; /**유저 닉네임*/
    String profileHero;
    int profileFrame;
    boolean dummyUser;

    public void refresh(String teamCharactersIds, String teamCharacterCodes, long score, int rankingtiertable_id, int arenaSeasonInfoId, String profileHero, int profileFrame, boolean dummyUser) {
        this.teamCharactersIds = teamCharactersIds;
        this.teamCharacterCodes = teamCharacterCodes;
        this.score = score;
        this.rankingtiertableId = rankingtiertable_id;
        this.arenaSeasonInfoId = arenaSeasonInfoId;
        this.profileHero = profileHero;
        this.profileFrame = profileFrame;
        this.dummyUser = dummyUser;
    }
    /** arenaSeasonInfoId 는 유저가 아레나를 플레이하고 결과에 따른 랭킹이 선정 되었을때 갱신 한다.*/
    public void refreshForNewSeason(long score, int rankingtiertable_id) {
        this.score = score;
        this.rankingtiertableId = rankingtiertable_id;
    }
    /** dummyUser용 ArenaSeasonInfoId 갱신*/
    public void SetDummyUserSeasonInfoId(int arenaSeasonInfoId){
        this.arenaSeasonInfoId = arenaSeasonInfoId;
    }
}
