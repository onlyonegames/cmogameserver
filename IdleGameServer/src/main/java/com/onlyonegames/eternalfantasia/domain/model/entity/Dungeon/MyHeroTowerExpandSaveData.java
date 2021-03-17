package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyHeroTowerExpandSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_saveDataValue;/*각층 오픈 정보, 플레이 결과 정보 Json*/
    String json_ExpandInfo/*확장 정보 (각 층별 결정된 보상 정보)*/;
    LocalDateTime seasonStartTime;

    public boolean IsResetSeasonStartTime() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(seasonStartTime, now);
        if(duration.toDays() >= 1) {
            //한국시 기준으로 오전 1시로 초기화
            seasonStartTime = LocalDateTime.of(now.minusHours(5).toLocalDate(), LocalTime.of(5,0,0));
            //다시 utc 로 변환 (한국 시간 utc +9 이므로 9시간 빼줌)
           // seasonStartTime = seasonStartTime.minusHours(9);
            return true;
        }
        return false;
    }

    public void ResetSaveDataValue(String json_saveDataValue) {
        this.json_saveDataValue = json_saveDataValue;
    }

    public void ResetExpandInfo(String json_ExpandInfo) {
        this.json_ExpandInfo = json_ExpandInfo;
    }
}
