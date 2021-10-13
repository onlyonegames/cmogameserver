package com.onlyonegames.eternalfantasia.domain.model.dto.Contents;

import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.ArenaRedisRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Contents.Leaderboard.PreviousArenaRanking;
import lombok.Data;

@Data
public class PreviousArenaRankingDto {
    Long id;
    Long useridUser;
    String userGameName;
    int point;
    int ranking;
    boolean receivable;

    public void InitFromPreviousDb(ArenaRanking dbData) {
        this.useridUser = dbData.getUseridUser();
        this.userGameName = dbData.getUserGameName();
        this.point = dbData.getPoint();
        this.ranking = dbData.getRanking();
    }

    public void InitFromRedisData(ArenaRedisRanking redisData, int ranking) {
        this.useridUser = redisData.getId();
        this.userGameName = redisData.getUserGameName();
        this.point = redisData.getPoint();
        this.ranking = ranking;
    }

    /**
     * 최초 생성시에만 사용해야함
     * receivable 무조건 true 세팅*/
    public PreviousArenaRanking ToEntity() {
        return PreviousArenaRanking.builder().useridUser(useridUser).userGameName(userGameName).point(point).ranking(ranking).receivable(true).build();
    }
}
