package com.onlyonegames.eternalfantasia.domain.model.dto.TavernVisitCompanionInfoData;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.util.MathHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TavernVisitCompanionInfoDataList {

    public static class TavernVisitCompanionInfoData {
        public String code;
        public String characterName;

        public float linkGaugePercent;
        public boolean recruited;
        // 인사하기
        public int greetingTryCurrentCount;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime greetingTryStartTime;

        //운영툴에서 사용하는 기능
        public void ChangeCompanion(String code, String characterName, float linkGaugePercent){
            this.characterName = characterName;
            this.code = code;
            this.linkGaugePercent = linkGaugePercent;
            this.recruited = false;
            this.greetingTryCurrentCount = 0;
            this.greetingTryStartTime = null;
            this.huntingStartTime = null;
            this.huntingTryCurrentCount = 0;
            this.giftTryStartTime = null;
            this.giftTryCurrentCount = 0;

        }

        public LocalDateTime GreetingTryStartTime() {

            if(greetingTryStartTime == null) {
                greetingTryStartTime = LocalDateTime.now();
                return greetingTryStartTime;
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime greetingTryStartDay = LocalDateTime.of(greetingTryStartTime.toLocalDate(), LocalTime.of(5,0,0));
            Duration duration = Duration.between(greetingTryStartDay, now);

            if(duration.toDays() >= 1) {
                greetingTryStartTime = now;
                /*인사 할수 있는 count 매일 초기화*/
                greetingTryCurrentCount = 0;
            }
            return greetingTryStartTime;

        }
        // 호감 표시
        public int huntingTryCurrentCount;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime huntingStartTime;

        public LocalDateTime HuntingStartTime() {
            if(huntingStartTime == null) {
                return huntingStartTime = LocalDateTime.now();
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime huntingStartDay = LocalDateTime.of(huntingStartTime.toLocalDate(), LocalTime.of(5,0,0));
            Duration duration = Duration.between(huntingStartDay, now);

            if(duration.toDays() >= 1) {
                huntingStartTime = now;
                /*호감 표시 count 매일 초기화*/
                huntingTryCurrentCount = 0;
            }
            return huntingStartTime;
        }

        // 선물 하기
        public int giftTryCurrentCount;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public LocalDateTime giftTryStartTime;

        LocalDateTime GiftTryStartTime() {

            if(giftTryStartTime == null) {
                return giftTryStartTime = LocalDateTime.now();
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime giftTryStartDay = LocalDateTime.of(giftTryStartTime.toLocalDate(), LocalTime.of(5,0,0));
            Duration duration = Duration.between(giftTryStartDay, now);

            /*선물 하기 시간과 count 매일 초기화*/
            if(duration.toDays() >= 1) {
                giftTryStartTime = now;
                giftTryCurrentCount = 0;
            }
            return giftTryStartTime;
        }

        public TavernVisitCompanionInfoData() {

        }

        public TavernVisitCompanionInfoData(herostable hero) {
            code = hero.getCode();
            characterName = hero.getName();
        }

        public void RelationsStart() {
            recruited = false;
            linkGaugePercent = 0;
            greetingTryCurrentCount = 0;
            greetingTryStartTime = null;
            greetingTryStartTime = GreetingTryStartTime();
            greetingTryStartTime = greetingTryStartTime.minusSeconds(30);
            huntingTryCurrentCount = 0;
            huntingStartTime = null;
            huntingStartTime = HuntingStartTime();
            huntingStartTime = huntingStartTime.minusSeconds(30);
            giftTryCurrentCount = 0;
            giftTryStartTime = null;
            giftTryStartTime = GiftTryStartTime();
            giftTryStartTime = giftTryStartTime.minusSeconds(30);
        }

        public void BreakeCompanion() {
            linkGaugePercent = 0;
            greetingTryCurrentCount = 0;
            huntingTryCurrentCount = 0;
            giftTryCurrentCount = 0;
        }

        boolean IsRecycleTimeForSyaHello(int recycleSecond) {

            greetingTryStartTime = GreetingTryStartTime();
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(greetingTryStartTime, now);
            if(duration.getSeconds() >= recycleSecond) {
                greetingTryStartTime = now;
                greetingTryStartTime.plusSeconds(recycleSecond);
                return true;
            }
            return false;
        }

        boolean IsRecycleTimeForHuntingTryCurrent(int recycleSecond) {

            huntingStartTime = HuntingStartTime();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(huntingStartTime, now);
            if(duration.getSeconds() >= recycleSecond) {
                huntingStartTime = now;
                huntingStartTime.plusSeconds(recycleSecond);
                return true;
            }
            return false;
        }

        boolean IsRecycleTimeForSendGift(int recycleSecond) {

            giftTryStartTime = GiftTryStartTime();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(giftTryStartTime, now);
            if(duration.getSeconds() >= recycleSecond) {
                giftTryStartTime = now;
                giftTryStartTime.plusSeconds(recycleSecond);
                return true;
            }
            return false;
        }

        public boolean SayHello() {
            if(false == IsRecycleTimeForSyaHello(5))
                return false;
            if(greetingTryCurrentCount >= 5)
                return false;
            if(linkGaugePercent == 1000)
                return false;
            greetingTryCurrentCount++;
            //linkGaugePercent += MathHelper.Range(1, 3);
            linkGaugePercent += MathHelper.Range(10, 20);
            linkGaugePercent = MathHelper.Clamp(linkGaugePercent, 0 , 1000);
            return true;
        }

        public boolean HuntingTryCurrent() {
            if(false == IsRecycleTimeForHuntingTryCurrent(5))
                return false;
            if(huntingTryCurrentCount >= 3)
                return false;
            if(linkGaugePercent == 1000)
                return false;
            huntingTryCurrentCount++;
            //linkGaugePercent += MathHelper.Range(1, 3);
            linkGaugePercent += MathHelper.Range(40, 60);
            linkGaugePercent = MathHelper.Clamp(linkGaugePercent, 0 , 1000);
            return true;
        }

        public boolean SendGift() {
            if(false == IsRecycleTimeForSendGift(5))
                return false;
            if(giftTryCurrentCount >= 2)
                return false;
            if(linkGaugePercent == 1000)
                return false;
            giftTryCurrentCount++;
            //linkGaugePercent += MathHelper.Range(1, 3);
            linkGaugePercent += MathHelper.Range(80, 100);
            linkGaugePercent = MathHelper.Clamp(linkGaugePercent, 0 , 1000);
            return true;
        }

        public boolean Recruit(){
            if(recruited)
                return false;
            recruited = true;
            return  true;
        }
    }

    public List<TavernVisitCompanionInfoData> visitList;
    /*현재 인연 맺기중인 동료의 vistList 인덱스 -1 이면 여관에 있는 동료리스트가 바뀐뒤 아직 인연맺기를 시작 하지 않은 경우임.*/
    public int relationHeroIndex;
    /*마지막 유저 액션이 방문중인 동료를 영입하는 행위였을때 해당값 true*/
    public boolean completedRecruit;
}
