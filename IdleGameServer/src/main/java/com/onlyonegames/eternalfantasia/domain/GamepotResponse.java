package com.onlyonegames.eternalfantasia.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GamepotResponse {
    private int status = 1;//1성공, 0 실패
    private String message = "";
}
