package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.eternalfantasia.etc.DefineLimitValue;
import com.onlyonegames.util.MathHelper;
import lombok.*;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyArenaSeasonSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
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
    boolean receiveableHallofHonorReward;//명예의 전당 보상 받았는지 체크

    boolean receiveableTierUpReward;//티어가 승급 되었다면 보상을 받을수 있음. 유저별 1번식만 받을수 있음.
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
    public void Play() {
        //if(playCountPerDayMax >= DefineLimitValue.LIMIT_MAX_ARENA_REPETITION_REWARD)
        //    return;
       // playCountPerDayMax++;
        playCountPerDay++;
        //playCountPerDay = MathHelper.Clamp(playCountPerDay, 0, DefineLimitValue.LIMIT_MAX_ARENA_REPETITION_REWARD);
    }

    public void Win(int rankingtiertable_id, Long score, Long seasonRank){
        winCount++;
        continueWinCount++;
        this.rankingtiertable_id = rankingtiertable_id;
        this.score = score;
        this.seasonRank = seasonRank;
        /*rankingtiertable_id 티어 아이디가 낮을수록 티어가 높은것임.*/
        if(rankingtiertable_id < highestRankingtiertable_id){
            highestRankingtiertable_id = rankingtiertable_id;
            receiveableTierUpReward = true;
        }
    }
    //반복 보상 획득을 위해 해당 카운트 소비
    public boolean SpendPlayCountPerDay(){
        if(playCountPerDay < 5)
            return false;
        playCountPerDay -= 5;
        return true;
    }

    public void Defeat(int rankingtiertable_id, Long score, Long seasonRank) {
        loseCount++;
        continueWinCount = 0;

        this.rankingtiertable_id = rankingtiertable_id;
        this.score = score;
        this.seasonRank = seasonRank;
        if(this.highestRankingtiertable_id==21){
            this.highestRankingtiertable_id = rankingtiertable_id;
        }
    }

    public void OffChangedSeasonFlag() {
        changedSeason = false;
    }
    public void ResetPreviousTierId(int previousTierId){
        this.previousTierId = previousTierId;
    }

    public void ResetChangedTierId(int changedTierId){
        this.changedTierId = changedTierId;
    }

    public void ReceiveTierUpReward(){
        receiveableTierUpReward = false;
        //achieveTierUpReward = true;
    }

    public void ReceiveDailyReward() {
        receiveableDailyReward = false;
        //achieveDailyReward = true;
    }
    public void ReceiveHallofHonorReward() { receiveableHallofHonorReward = false; }
    public void ResetSeasonSaveData(){
        rankingtiertable_id = 21;/*가장 낮은 티어 bronz5 티어 아이디 20*/
        winCount = 0;
        loseCount = 0;
        continueWinCount = 0;
        playCountPerDay = 0;
        playCountPerDayMax = 0;
        score = 0L;
        seasonRank = 0L;
      //  receivedPlayCountPerDayReward = false;
        receiveableDailyReward = true;
        receiveableHallofHonorReward = true;
        //클라이언트에서 시즌이 변경 되었다는 것을 알수 있도록 플래그 설정.
        changedSeason = true;
    }

//    public boolean IsResetSeasonStartTime() {
//
//        int seasonStartYear = seasonStartTime.getYear();
//        int seasonMonthValue = seasonStartTime.getMonthValue();
//        LocalDateTime now = LocalDateTime.now();
//
//        int nowYear = now.getYear();
//        int nowMonthValue = now.getMonthValue();
//
//        if(nowMonthValue != seasonMonthValue || nowYear != seasonStartYear) {
//            //서버 타임 기준으로 오전 1시로 초기화
//            seasonStartTime = LocalDateTime.of(now.getYear(),now.getMonth(),1, 0, 0, 0);
//            //다시 utc 로 변환 (한국 시간 utc +9 이므로 9시간 빼줌)
//           // seasonStartTime = seasonStartTime.minusHours(9);
//            return true;
//        }
//        return false;
//    }

    public LocalDateTime DailyTime() {
        LocalDateTime now = LocalDateTime.now();
        if(dailyTime == null) {
            dailyTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            /*하루 반복 대전 카운트. 매일 초기화*/
            playCountPerDay = 0;
            playCountPerDayMax = 0;
           // receivedPlayCountPerDayReward = false;
            /*하루 한번씩 자신의 티어에 맞는 보상 받기 가능*/
            receiveableDailyReward = true;
            receiveableHallofHonorReward = true;
            return dailyTime;
        }
        LocalDateTime greetingTryStartDay = LocalDateTime.of(dailyTime.toLocalDate(), LocalTime.of(5,0,0));
        Duration duration = Duration.between(greetingTryStartDay, now);

        if(duration.toDays() >= 1) {
            dailyTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            /*하루 반복 대전 카운트. 매일 초기화*/
            playCountPerDay = 0;
            playCountPerDayMax = 0;
            //receivedPlayCountPerDayReward = false;
            /*하루 한번씩 자신의 티어에 맞는 보상 받기 가능*/
            receiveableDailyReward = true;
            receiveableHallofHonorReward = true;
        }
        return dailyTime;
    }

    public void InitData() {
        this.seasonRank = 0L;
        this.score = 0L;
        this.highestRankingtiertable_id = 21;
        this.rankingtiertable_id = 21;
        this.winCount = 0;
        this.loseCount = 0;
        this.continueWinCount = 0;
        this.playCountPerDay = 0;
        this.playCountPerDayMax = 0;
        this.receiveableDailyReward = false;
        this.receiveableTierUpReward = false;
        this.receiveableHallofHonorReward = false;
        this.previousTierId = 20;
        this.changedTierId = 20;
        this.changedSeason = false;
    }

    public void ResetSeasonNo(int seasonNo) {
        this.seasonNo = seasonNo;
    }

    public void ResetSeasonStartTime(LocalDateTime seasonStartTime) {
        this.seasonStartTime = seasonStartTime;
    }

    public void ResetSeasonEndTime(LocalDateTime seasonEndTime) {
        this.seasonEndTime = seasonEndTime;
    }

    public void TurnOnSeasonReady() {
        this.seasonReady = true;
    }

    public void TurnOffSeasonReady() {
        this.seasonReady = false;
    }

    //운영툴용
    public void SetScore(Long score) {
        this.score = score;
    }
    public void SetHighestRanking(int highestRankingtiertable_id) {
        this.highestRankingtiertable_id = highestRankingtiertable_id;
    }
    public void SetPreviousTier(int previousTierId) {
        this.previousTierId = previousTierId;
    }
}
