package com.onlyonegames.eternalfantasia.domain.model.dto.Inventory;

import com.onlyonegames.eternalfantasia.domain.model.dto.AccessoryOptionJsonDto;
import lombok.Data;

import java.util.List;

@Data
public class ClassOptionJsonDto {
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
