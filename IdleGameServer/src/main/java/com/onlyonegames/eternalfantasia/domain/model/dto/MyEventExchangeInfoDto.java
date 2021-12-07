package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyEventExchangeInfo;
import lombok.Data;

@Data
public class MyEventExchangeInfoDto {
    Long id;
    Long useridUser;
    int goldCount; //일반 골드
    int advancedGoldCount; //고급 골드
    int soulStoneCount; //일반 영혼석
    int advancedSoulStoneCount; //고급 영혼석
    int fragmentCount; // 증폭파편
    int dungeonTicketCount; //일반 던전티켓
    int advancedDungeonTicketCount; //고급 던전티켓
    int crystalCount; //고대결정
    int randomBasicAccessoryCount; //일반 장신구
    int randomNewAccessoryCount; // 연금 장신구
    int legendClassCount; //전설 클래스
    int divineClassCount; //신화 클래스
    int costumeACount; // 코스튬 A
    int costumeBCount; //코스튬 B
    int divineRandomBEquipment; //신화 B 장비
    int divineRandomDRune; //신화 D 보석
    int randomOrb; //랜덤 오브
    int ancientRandomBEquipment; //고대 B 장비

    private void Init() {
        this.goldCount = 2; //일반 골드
        this.advancedGoldCount = 2; //고급 골드
        this.soulStoneCount = 2; //일반 영혼석
        this.advancedSoulStoneCount = 5; //고급 영혼석
        this.fragmentCount = 3; // 증폭파편
        this.dungeonTicketCount = 10; //일반 던전티켓
        this.advancedDungeonTicketCount = 5; //고급 던전티켓
        this.crystalCount = 3; //고대결정
        this.randomBasicAccessoryCount = 20; //일반 장신구
        this.randomNewAccessoryCount = 10; // 연금 장신구
        this.legendClassCount = 8; //전설 클래스
        this.divineClassCount = 1; //신화 클래스
        this.costumeACount = 1; // 코스튬 A
        this.costumeBCount = 1; //코스튬 B
        this.divineRandomBEquipment = 10; //신화 B 장비
        this.divineRandomDRune = 10; //신화 D 보석
        this.randomOrb = 10; //랜덤 오브
        this.ancientRandomBEquipment = 1; //고대 B 장비
    }

    public MyEventExchangeInfo ToEntity() {
        Init();
        return MyEventExchangeInfo.builder().useridUser(useridUser).goldCount(goldCount).advancedGoldCount(advancedGoldCount).soulStoneCount(soulStoneCount).advancedSoulStoneCount(advancedSoulStoneCount)
                .fragmentCount(fragmentCount).dungeonTicketCount(dungeonTicketCount).advancedDungeonTicketCount(advancedDungeonTicketCount).crystalCount(crystalCount).randomBasicAccessoryCount(randomBasicAccessoryCount)
                .randomNewAccessoryCount(randomNewAccessoryCount).legendClassCount(legendClassCount).divineClassCount(divineClassCount).costumeACount(costumeACount).costumeBCount(costumeBCount)
                .divineRandomBEquipment(divineRandomBEquipment).divineRandomDRune(divineRandomDRune).randomOrb(randomOrb).ancientRandomBEquipment(ancientRandomBEquipment).build();
    }
}
