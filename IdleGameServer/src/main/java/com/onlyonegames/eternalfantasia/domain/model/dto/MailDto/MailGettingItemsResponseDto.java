package com.onlyonegames.eternalfantasia.domain.model.dto.MailDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto.GiftItemDtosList;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyCharactersBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GoodsItemDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto.GotchaCharacterResponseDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MailGettingItemsResponseDto {
    /*골드*/
    int gettingGold;
    /*다이아*/
    int gettingDiamond;
    /*링크포인트*/
    int gettingLinkPoint;
    /*아레나 코인*/
    int gettingArenaCoin;
    /*아레나 티켓*/
    int gettingArenaTicket;
    /*용의 비늘*/
    int gettingLowDragonScale;
    int gettingMiddleDragonScale;
    int gettingHighDragonScale;
    /*다크오브*/
    int gettingDarkObe;

    /*재료, 제련석, 강화석*/
    List<BelongingInventoryDto> changedBelongingInventoryList = new ArrayList<>();
    /*선물*/
    List<GiftItemDtosList.GiftItemDto> changedMyGiftInventoryList = new ArrayList<>();
    /*장비*/
    List<HeroEquipmentInventoryDto> changedHeroEquipmentInventoryList = new ArrayList<>();
    /*케릭터*/
    GotchaCharacterResponseDto gotchaCharacterResponseDto = new GotchaCharacterResponseDto();

    public void AddGettingGold(int gettingGold) {
        this.gettingGold += gettingGold;
    }

    public void AddGettingDiamond(int gettingDiamond) {
        this.gettingDiamond += gettingDiamond;
    }

    public void AddGettingLinkPoint(int gettingLinkPoint) {
        this.gettingLinkPoint += gettingLinkPoint;
    }

    public void AddGettingArenaCoin(int gettingArenaCoin) {
        this.gettingArenaCoin += gettingArenaCoin;
    }

    public void AddGettingArenaTicket(int gettingArenaTicket) { this.gettingArenaTicket += gettingArenaTicket; }

    public void AddGettingLowDragonScale(int gettingDragonScale) { this.gettingLowDragonScale += gettingDragonScale; }

    public void AddGettingMiddleDragonScale(int gettingDragonScale) { this.gettingMiddleDragonScale += gettingDragonScale; }

    public void AddGettingHighDragonScale(int gettingDragonScale) { this.gettingHighDragonScale += gettingDragonScale; }

    public void AddGettingDarkObe(int gettingDarkObe) {
        this.gettingDarkObe += gettingDarkObe;
    }
}
