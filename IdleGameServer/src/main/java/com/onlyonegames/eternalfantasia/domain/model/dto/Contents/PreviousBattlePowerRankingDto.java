package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.BattlePowerRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousBattlePowerRanking;
import lombok.Data;

@Data
public class PreviousBattlePowerRankingDto {
    Long id;
    Long useridUser;
    String userGameName;
    Long battlePower;
    int ranking;
    boolean receivable;

    public void InitFromPreviousDb(BattlePowerRanking dbData) {
        this.useridUser = dbData.getUseridUser();
        this.userGameName = dbData.getUserGameName();
        this.battlePower = dbData.getBattlePower();
        this.ranking = dbData.getRanking();
    }

    public void InitFromRedisData(BattlePowerRedisRanking redisData, int ranking) {
        this.useridUser = redisData.getId();
        this.userGameName = redisData.getUserGameName();
        this.battlePower = redisData.getBattlePower();
        this.ranking = ranking;
    }

    /**
     * 최초 생성시에만 사용해야함
     * receivable 무조건 true 세팅*/
    public PreviousBattlePowerRanking ToEntity() {
        return PreviousBattlePowerRanking.builder().useridUser(useridUser).userGameName(userGameName).battlePower(battlePower).ranking(ranking).receivable(true).build();
    }
}
