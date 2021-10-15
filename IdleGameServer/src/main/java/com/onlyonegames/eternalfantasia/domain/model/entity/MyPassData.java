package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyPassData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    public String json_attendanceSaveData;
    public String json_daySaveData;
    public Long gettingCount;
    public String json_levelSaveData;
    public String json_stageSaveData;
    public LocalDateTime lastAttendanceDate;

    public void ResetAttendanceJsonData(String json_attendanceSaveData) {
        this.json_attendanceSaveData = json_attendanceSaveData;
    }

    public void ResetDayJsonData(String json_daySaveData) {
        this.json_daySaveData = json_daySaveData;
    }

    public void ResetLevelJsonData(String json_levelSaveData) {
        this.json_levelSaveData = json_levelSaveData;
    }

    public void ResetStageSaveData(String json_stageSaveData) {
        this.json_stageSaveData = json_stageSaveData;
    }

    public void ResetLastAttendanceDate(LocalDateTime lastAttendanceDate) {
        this.lastAttendanceDate = lastAttendanceDate;
    }

    public void SetGettingCount() {
        this.gettingCount = 0L;
    }
}
