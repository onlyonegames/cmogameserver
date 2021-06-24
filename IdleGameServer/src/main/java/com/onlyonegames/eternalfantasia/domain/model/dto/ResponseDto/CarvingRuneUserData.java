package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPixieInfoData;
import lombok.Data;

@Data
public class CarvingRuneUserData {
    public int runeSlot1;
    public int runeSlot2;
    public int runeSlot3;
    public int runeSlot4;
    public int runeSlot5;
    public int runeSlot6;

    public void SetCarvingRuneUserData(MyPixieInfoData data){
        this.runeSlot1 = data.getRuneSlot1();
        this.runeSlot2 = data.getRuneSlot2();
        this.runeSlot3 = data.getRuneSlot3();
        this.runeSlot4 = data.getRuneSlot4();
        this.runeSlot5 = data.getRuneSlot5();
        this.runeSlot6 = data.getRuneSlot6();
    }
}
