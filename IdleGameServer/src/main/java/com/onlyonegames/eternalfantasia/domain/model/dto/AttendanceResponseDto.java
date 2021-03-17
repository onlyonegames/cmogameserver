package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

@Data
public class AttendanceResponseDto {
    public boolean receivable;
    public int attendanceIndex;

    public void SetAttendanceResponseDto(boolean receivable, int attendanceIndex){
        this.receivable = receivable;
        this.attendanceIndex = attendanceIndex;
    }
}
