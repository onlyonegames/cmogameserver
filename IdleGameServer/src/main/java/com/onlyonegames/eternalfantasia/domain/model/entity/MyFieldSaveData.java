package com.onlyonegames.eternalfantasia.domain.model.entity;
import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
/*
필드에 몬스터,보물상자,광산등의 오브젝트들에 대한 정보와 현재 유저가 선택한 챕터(필드)번호,비공정 탑승 정보를 관리.
데이터싱크는 유저가 맵을 선택했을때, 비공정 탑승과 하차때, 필드에 오브젝트들이 생성되었을때, 해당 오브젝트를 입수할때이다.

* */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyFieldSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser; //유저 아이디
    String json_saveDataValue;/*필드상황저장데이터*/
    String json_hotTimeSaveDataValue;/*HotTimeEventSaveData*/
    String json_fieldExchangeItemSaveDataValue;
    LocalDateTime lastClearTime;/*필드오브젝트들 하루마다 일괄 초기화 하기 위해 마지막으로 초기화 한 시간 저장*/
    public boolean IsResetTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastClearTime, now);
        if(duration.toDays() >= 1) {
            lastClearTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            return true;
        }
        return false;
    }
//    public boolean IsResetTime() {
//        LocalDateTime now = LocalDateTime.now();
//        Duration duration = Duration.between(lastClearTime, now);
//        if(duration.toMinutes() >= 1) {
//            lastClearTime = LocalDateTime.now();
//            return true;
//        }
//        return false;
//    }

    public void ResetSaveDataValue(String json_saveDataValue) {
        this.json_saveDataValue = json_saveDataValue;
    }
    public void ResetHotTimeSaveDataValue(String json_hotTimeSaveDataValue) { this.json_hotTimeSaveDataValue = json_hotTimeSaveDataValue; }
    public void ResetFieldExchangeItemSaveDataValue(String json_fieldExchangeItemSaveDataValue) { this.json_fieldExchangeItemSaveDataValue = json_fieldExchangeItemSaveDataValue; }
}
