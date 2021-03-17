package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.CostumeDto.CostumeDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto.LinkforceOpenDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto.LinkweaponInfoDtosList;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentDeck;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyMainHeroSkill;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MyArenaUsersDataDto {
    
    @AllArgsConstructor
    public static class MyArenaUserData {
        public List<MyCharacters> teamDeck;
        public List<LinkforceOpenDtosList.CompanionLinkforceOpenInfo> myLinkforceInfoList;
        public List<LinkweaponInfoDtosList.CompanionLinkweaponInfo> myLinkweaponInfoList;
        public List<CostumeDtosList.CostumeDto> myCostumeInventory;
        public List<HeroEquipmentInventory> heroEquipmentInventoryList;
        public MyEquipmentDeck myEquipmentDeck;
        public List<MyMainHeroSkill> myMainHeroSkillList;
    }

    public MyArenaUserData myArenaUserData;
    public MyArenaUserData enemyArenaUserData;
}

