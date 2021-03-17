package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyArenaSeasonSaveData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyArenaSeasonSaveDataDto {
    Long id;
    Long useridUser;
    Long seasonRank;
    Long score;
    //랭킹전에서 달성 했던 가장 높은 등급. 등급 떨어졌다가 다시 올린 경우, 시즌 초기화되었다가 다시 올린경우등  등급 상승 보상을 두번 받는 경우를 제외하기 위함.
    int highestRankingtiertable_id;
    int rankingtiertable_id; //티어
    int winCount;//승 카운트
    int loseCount;//패 카운트
    int continueWinCount;//연승 정보(연승중 패배하면 0, 최소2승을 하면 그때부터 2연승이라고 표현)
    int playCountPerDay;//하루 반복 대전 카운트. 매일 초기화 되고, 아레나 플레이마다 카운트가 기록되며 해당 카운트가 일정수치 이상 되면 아레나 코인을 보상으로 준다.
    int playCountPerDayMax;//반복 대전 카운트가 하루 최고치인 50을 달성 하면 더이상 playCountPerDay가 올라가지 않는다.
    boolean receiveableDailyReward;//일일보상 받았는지 체크
    boolean receiveableTierUpReward;//티어가 승급 되었다면 보상을 받을수 있음. 유저별 1번식만 받을수 있음.
    boolean receiveableHallofHonorReward;//명예의 전당 보상 받았는지 체크

    /**아래의 두 티어값을 비교해서 다르면 티어가 변경 되었다는 표현을 진행.*/
    public int previousTierId;//플레이 직전 티어.
    public int changedTierId;//플레이 이후 티어.
    /**시즌이 변경 되었다는 플래그 */
    boolean changedSeason;//시즌이 변경 되는 타임에 true. 시즌변경후 보상받기 API 호출시 false로 변경됨.
    /**현재 시즌 번호.*/
    int seasonNo;
    /**시즌 준비*/
    boolean seasonReady;

    LocalDateTime dailyTime;//매일 리셋되는 정보들을 위해 매일 갱신.
    LocalDateTime seasonStartTime;//매주 월요일마다 갱신.
    LocalDateTime seasonEndTime;
    public MyArenaSeasonSaveData ToEntity() {
        return MyArenaSeasonSaveData.builder().useridUser(useridUser).seasonRank(seasonRank).score(score).highestRankingtiertable_id(highestRankingtiertable_id).rankingtiertable_id(rankingtiertable_id)
                .winCount(winCount).loseCount(loseCount).continueWinCount(continueWinCount).playCountPerDay(playCountPerDay).playCountPerDayMax(playCountPerDayMax).receiveableDailyReward(receiveableDailyReward).receiveableTierUpReward(receiveableTierUpReward)
                .receiveableHallofHonorReward(receiveableHallofHonorReward).previousTierId(previousTierId).changedTierId(changedTierId).changedSeason(changedSeason).seasonNo(seasonNo).dailyTime(dailyTime).seasonStartTime(seasonStartTime).seasonEndTime(seasonEndTime).seasonReady(seasonReady).build();
    }

    public void SetDto(MyArenaSeasonSaveData myArenaSeasonSaveData) {
        this.id = myArenaSeasonSaveData.getId();
        this.useridUser = myArenaSeasonSaveData.getUseridUser();
        this.seasonRank = myArenaSeasonSaveData.getSeasonRank();
        this.score = myArenaSeasonSaveData.getScore();
        this.highestRankingtiertable_id = myArenaSeasonSaveData.getHighestRankingtiertable_id();
        this.rankingtiertable_id = myArenaSeasonSaveData.getRankingtiertable_id();
        this.winCount = myArenaSeasonSaveData.getWinCount();
        this.loseCount = myArenaSeasonSaveData.getLoseCount();
        this.continueWinCount = myArenaSeasonSaveData.getContinueWinCount();
        this.playCountPerDay = myArenaSeasonSaveData.getPlayCountPerDay();
        this.playCountPerDayMax = myArenaSeasonSaveData.getPlayCountPerDayMax();
        this.receiveableDailyReward = myArenaSeasonSaveData.isReceiveableDailyReward();
        this.receiveableTierUpReward = myArenaSeasonSaveData.isReceiveableTierUpReward();
        this.receiveableHallofHonorReward = myArenaSeasonSaveData.isReceiveableHallofHonorReward();
        this.seasonReady = myArenaSeasonSaveData.isSeasonReady();
        this.seasonStartTime = myArenaSeasonSaveData.getSeasonStartTime();
        this.seasonEndTime = myArenaSeasonSaveData.getSeasonEndTime();
        this.previousTierId = myArenaSeasonSaveData.getPreviousTierId();
        this.changedTierId = myArenaSeasonSaveData.getChangedTierId();
        this.seasonNo = myArenaSeasonSaveData.getSeasonNo();
        this.dailyTime = myArenaSeasonSaveData.getDailyTime();
    }

    public void InitFromDbData(MyArenaSeasonSaveData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.seasonRank = dbData.getSeasonRank();
        this.score = dbData.getScore();
        this.highestRankingtiertable_id = dbData.getHighestRankingtiertable_id();
        this.rankingtiertable_id = dbData.getRankingtiertable_id();
        this.winCount = dbData.getWinCount();
        this.loseCount = dbData.getLoseCount();
        this.continueWinCount = dbData.getContinueWinCount();
        this.playCountPerDay = dbData.getPlayCountPerDay();
        this.playCountPerDayMax = dbData.getPlayCountPerDayMax();
        this.receiveableDailyReward = dbData.isReceiveableDailyReward();
        this.receiveableTierUpReward = dbData.isReceiveableTierUpReward();
        this.receiveableHallofHonorReward = dbData.isReceiveableHallofHonorReward();
        this.seasonReady = dbData.isSeasonReady();
        this.seasonStartTime = dbData.getSeasonStartTime();
        this.seasonEndTime = dbData.getSeasonEndTime();
        this.previousTierId = dbData.getPreviousTierId();
        this.changedTierId = dbData.getChangedTierId();
        this.seasonNo = dbData.getSeasonNo();
        this.dailyTime = dbData.getDailyTime();
    }
}
