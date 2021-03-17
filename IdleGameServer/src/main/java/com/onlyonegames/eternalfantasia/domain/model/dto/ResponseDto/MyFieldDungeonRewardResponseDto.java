package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class MyFieldDungeonRewardResponseDto {
    public List<MyFieldDungeonRewardDto> myFieldDungeonRewardDtoList = new ArrayList<>();

    public static class MyFieldDungeonRewardDto{
        public int grade;
        /*골드*/
        public int gettingGold;
        /*다이아*/
        public int gettingDiamond;
        /*링크포인트*/
        public int gettingLinkPoint;
        /*아레나 코인*/
        public int gettingArenaCoin;
        /*아레나 티켓*/
        public int gettingArenaTicket;
        /*용의 비늘*/
        public int gettingLowDragonScale;
        public int gettingMiddleDragonScale;
        public int gettingHighDragonScale;

        /*재료, 제련석, 강화석*/
        public List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();
        /*선물*/
        public GiftItemDtosList.GiftItemDto changedMyGiftInventoryList = new GiftItemDtosList.GiftItemDto();
        /*장비*/
        public HeroEquipmentInventoryDto changedHeroEquipmentInventoryList = new HeroEquipmentInventoryDto();
        /*케릭터*/
        public GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();
    }
}
