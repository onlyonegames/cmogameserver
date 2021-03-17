package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import com.onlyonegames.util.StringMaker;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyProductionMastery extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long userIdUser;
    int masteryLevel;/*장비 제작 숙련도 MaxLevel = 100*/
    int remainLevel;/*최대 숙련도를 찍고 남은 레벨들, cycle을 넘긴후 masteryLevel로 전환된다*/
    String openedGifts;/*특정 숙련도 달성시 획득 하게되는 선물들중 받을 순서*/
    long masteryCycle;/*장비 제작 숙련도 다 채우고 보상 받은후 다시 숙련도 초기화 된 횟수*/

    @Builder
    public MyProductionMastery(Long userIdUser) {
        this.userIdUser = userIdUser;
        Init();
    }

    public void Init() {
        masteryLevel = 0;
        openedGifts = "0,0,0";
        masteryCycle = 0;
    }

    private void NextCycle() {
        masteryCycle++;
        masteryLevel = remainLevel;
        openedGifts = "0,0,0";
        remainLevel = 0;
    }

    public boolean ReceiveReward(int receiveIndex) {
        String[] opendGiftsArray = openedGifts.split(",");
        int opendGift = Integer.parseInt(opendGiftsArray[receiveIndex]);
        if(opendGift > 0)
            return false;
        int maxLevel = 100;

        opendGiftsArray[receiveIndex] = "1";
        StringMaker.Clear();
        for (int i = 0; i < opendGiftsArray.length; i++) {
            if (i > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(opendGiftsArray[i]);
        }
        openedGifts = StringMaker.stringBuilder.toString();
        if(masteryLevel == maxLevel && openedGifts.equals("1,1,1")) {
            NextCycle();
        }
        return true;
    }

    public boolean IsMaxLevel() {
        int maxLevel = 100;
        return masteryLevel == maxLevel;
    }

    public boolean LevelUp(int plusLevel) {
        int maxLevel = 100;
        if(masteryLevel == maxLevel)
            return false;
        int level = masteryLevel + plusLevel;
        if(level > maxLevel) {
            remainLevel = level - maxLevel;
            level = maxLevel;
        }
        masteryLevel = level;
        return true;
    }
}
