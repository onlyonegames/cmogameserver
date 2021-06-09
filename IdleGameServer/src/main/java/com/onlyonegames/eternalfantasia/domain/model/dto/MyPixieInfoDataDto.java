package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyPixieInfoData;
import lombok.Data;

@Data
public class MyPixieInfoDataDto {
    Long id;
    Long useridUser;
    int level;
    Long exp;
    Long maxExp;
    Long runeSlot1;
    Long runeSlot2;
    Long runeSlot3;
    Long runeSlot4;
    Long runeSlot5;
    Long runeSlot6;

    public MyPixieInfoData ToEntity() {
        return MyPixieInfoData.builder().useridUser(useridUser).level(level).exp(exp).maxExp(maxExp)
                .runeSlot1(runeSlot1).runeSlot2(runeSlot2).runeSlot3(runeSlot3).runeSlot4(runeSlot4).runeSlot5(runeSlot5).runeSlot6(runeSlot6).build();
    }

    public void SetFirstData(Long useridUser){
        this.useridUser = useridUser;
        InitData();
    }

    public void InitData(){
        this.level = 1;
        this.exp = 0L;
        this.maxExp = 10L;
        this.runeSlot1 = 0L;
        this.runeSlot2 = 0L;
        this.runeSlot3 = 0L;
        this.runeSlot4 = 0L;
        this.runeSlot5 = 0L;
        this.runeSlot6 = 0L;
    }

    public void InitFromDBData(MyPixieInfoData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.level = dbData.getLevel();
        this.exp = dbData.getExp();
        this.maxExp = dbData.getMaxExp();
        this.runeSlot1 = dbData.getRuneSlot1();
        this.runeSlot2 = dbData.getRuneSlot2();
        this.runeSlot3 = dbData.getRuneSlot3();
        this.runeSlot4 = dbData.getRuneSlot4();
        this.runeSlot5 = dbData.getRuneSlot5();
        this.runeSlot6 = dbData.getRuneSlot6();
    }
}
