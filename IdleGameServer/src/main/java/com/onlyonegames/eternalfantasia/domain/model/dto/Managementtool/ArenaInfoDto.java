package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import lombok.Data;

@Data
public class ArenaInfoDto {
    public Long id;
    public Long userId;
    public Long seasonRank;
    public Long score;
    public String highestRankingtierTable_id;
    public String rankingtierTable_id;
    public int winCount;
    public int loseCount;
    public int continueWinCount;
    public int playCountPerDay;
    public int playCountPerDayMax;
    public boolean receiveableDailyReward;
    public boolean receiveableTierUpReward;
    public boolean receiveableHallofHonorReward;
    public int seasonNo;

    public void setArenaInfo(MyArenaSeasonSaveData data, String highestRankingtierTable_id, String rankingtierTable_id) {
        this.id = data.getId();
        this.userId = data.getUseridUser();
        this.seasonRank = data.getSeasonRank();
        this.score = data.getScore();
        this.highestRankingtierTable_id = highestRankingtierTable_id;
        this.rankingtierTable_id = rankingtierTable_id;
        this.winCount = data.getWinCount();
        this.loseCount = data.getLoseCount();
        this.continueWinCount = data.getContinueWinCount();
        this.playCountPerDay = data.getPlayCountPerDay();
        this.playCountPerDayMax = data.getPlayCountPerDayMax();
        this.receiveableDailyReward = data.isReceiveableDailyReward();
        this.receiveableTierUpReward = data.isReceiveableTierUpReward();
        this.receiveableHallofHonorReward = data.isReceiveableHallofHonorReward();
        this.seasonNo = data.getSeasonNo();
    }
}
