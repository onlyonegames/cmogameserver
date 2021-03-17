package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyGiftInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GotchaResponseDto {
    /*재료, 제련석, 강화석*/
    List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();
    /*제련티켓, 링크포인트*/
    List<GoodsItemDto> changedGoodsList = new ArrayList<>();
    /*장비,  */
    List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = new ArrayList<>();
}
