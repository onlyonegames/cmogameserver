package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyRuneLevelInfoData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class PlayerInfoDto {
    public String gold;
    public int diamond;
    public String soulStone;
    public int skillPoint;
    public int moveStone;
    public int runeLevel;
    public List<MyRuneInventoryDto> myRuneInventoryDtoList;

    public void SetPlayerInfoDto(User user, MyRuneLevelInfoData myRuneLevelInfoData, List<MyRuneInventoryDto> myRuneInventoryDtoList) {
        this.gold = user.getGold();
        this.diamond = user.getDiamond();
        this.soulStone = user.getSoulStone();
        this.skillPoint = user.getSkillPoint();
        this.moveStone = user.getMoveStone();
        this.runeLevel = myRuneLevelInfoData.getLevel();
        this.myRuneInventoryDtoList = myRuneInventoryDtoList;
    }
}
