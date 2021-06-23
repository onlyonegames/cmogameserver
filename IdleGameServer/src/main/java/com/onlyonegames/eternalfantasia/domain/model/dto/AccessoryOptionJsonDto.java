package com.onlyonegames.eternalfantasia.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AccessoryOptionJsonDto {
    public static class Options{
        public int grade;
        public int index;
    }

    public List<Options> options;
}
