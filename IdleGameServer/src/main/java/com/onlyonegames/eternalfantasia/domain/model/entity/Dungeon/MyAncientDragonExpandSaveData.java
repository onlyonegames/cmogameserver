package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyAncientDragonExpandSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String json_saveDataValue;/*각층 오픈 정보, 플레이 결과 정보 Json*/
    String selectedDragonCode; /*요일마다 다른 드래곤*/
    int playableRemainCount;/*매 시즌 고대던전은 총 3번만 플레이 가능*/
    LocalDateTime seasonStartTime;

    public boolean IsResetSeasonStartTime() {
        LocalDateTime now = LocalDateTime.now();
        if(seasonStartTime == null) {
            seasonStartTime = now.minusDays(1);
            //한국시 기준으로 오전 1시로 초기화
            seasonStartTime = LocalDateTime.of(now.toLocalDate(), LocalTime.of(5,0,0));
            //다시 utc 로 변환 (한국 시간 utc +9 이므로 9시간 빼줌)
           // seasonStartTime = seasonStartTime.minusHours(9);
        }

        Duration duration = Duration.between(seasonStartTime, now);
        if(duration.toDays() >= 1) {
            playableRemainCount = 3;
            //한국시 기준으로 오전 1시로 초기화
            seasonStartTime = LocalDateTime.of(now.minusHours(5).toLocalDate(),  LocalTime.of(5,0,0));
            //다시 utc 로 변환 (한국 시간 utc +9 이므로 9시간 빼줌)
           // seasonStartTime = seasonStartTime.minusHours(9);
            DayOfWeek dayOfWeek = seasonStartTime.getDayOfWeek();
            switch (dayOfWeek) {
                case MONDAY:
                    selectedDragonCode = "mn_053";
                    break;
                case TUESDAY:
                    selectedDragonCode = "mn_050";
                    break;
                case WEDNESDAY:
                    selectedDragonCode = "mn_051";
                    break;
                case THURSDAY:
                    selectedDragonCode = "mn_054";
                    break;
                case FRIDAY:
                    selectedDragonCode = "mn_065";
                    break;
                case SATURDAY:
                    selectedDragonCode = "mn_052";
                    break;
                case SUNDAY:
                    selectedDragonCode = "mn_076";
                    break;
            }
            return true;
        }
        return false;
    }

    public void Clear() {
        if(playableRemainCount > 0)
            playableRemainCount--;
    }

    public void ResetPlayable() {
        this.playableRemainCount = 3;
    }

    public void ResetSaveDataValue(String json_saveDataValue) {
        this.json_saveDataValue = json_saveDataValue;
    }

    public void SetPlayableRemainCountForTest() {
        playableRemainCount = 3;
    }
}
