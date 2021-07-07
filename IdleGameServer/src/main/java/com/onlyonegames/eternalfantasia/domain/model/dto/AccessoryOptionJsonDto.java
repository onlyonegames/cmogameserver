package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccessoryOptionJsonDto {
    public static class OptionInfo{
        public int grade;
        public int index;

        public void SetOptionInfo(int grade, int index) {
            this.grade = grade;
            this.index = index;
        }
    }

    public List<OptionInfo> options;
}
