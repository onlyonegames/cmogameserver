package com.onlyonegames.eternalfantasia.domain.model.entity;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyPixieInfoData extends BaseTimeEntity {
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long useridUser;
    public int level;
    public Long exp;
    public int runeSlot1;
    public int runeSlot2;
    public int runeSlot3;
    public int runeSlot4;
    public int runeSlot5;
    public int runeSlot6;

//    public void GetExp(Long addExp) {
//        Long temp = addExp;
//        do{
//            if(this.exp + temp >= this.maxExp){
//                temp -= this.maxExp - this.exp;
//                this.exp = 0L;
//                this.level += 1;
//                this.maxExp += this.level * (3 * this.level + 1);
//            } else {
//                this.exp += temp;
//                temp -= temp;
//            }
//        }while (temp != 0L);
//
//    }

//    public Long EquipmentRune(Long runeInventoryId, int slotNo){
//        Long temp = 0L;
//        switch(slotNo){
//            case 1:
//                if(!runeSlot1.equals(0))
//                    temp = runeSlot1;
//                runeSlot1 = runeInventoryId;
//                break;
//            case 2:
//                if(!runeSlot2.equals(0))
//                    temp = runeSlot2;
//                runeSlot2 = runeInventoryId;
//                break;
//            case 3:
//                if(!runeSlot3.equals(0))
//                    temp = runeSlot3;
//                runeSlot3 = runeInventoryId;
//                break;
//            case 4:
//                if(!runeSlot4.equals(0))
//                    temp = runeSlot4;
//                runeSlot4 = runeInventoryId;
//                break;
//            case 5:
//                if(!runeSlot5.equals(0))
//                    temp = runeSlot5;
//                runeSlot5 = runeInventoryId;
//                break;
//            case 6:
//                if(!runeSlot6.equals(0))
//                    temp = runeSlot6;
//                runeSlot6 = runeInventoryId;
//                break;
//
//        }
//        return temp;
//    }
//
//    public Long UnEquipmentRune(int slotNo) {
//        Long temp = 0L;
//        switch(slotNo){
//            case 1:
//                if(!runeSlot1.equals(0))
//                    temp = runeSlot1;
//                runeSlot1 = 0L;
//                break;
//            case 2:
//                if(!runeSlot2.equals(0))
//                    temp = runeSlot2;
//                runeSlot2 = 0L;
//                break;
//            case 3:
//                if(!runeSlot3.equals(0))
//                    temp = runeSlot3;
//                runeSlot3 = 0L;
//                break;
//            case 4:
//                if(!runeSlot4.equals(0))
//                    temp = runeSlot4;
//                runeSlot4 = 0L;
//                break;
//            case 5:
//                if(!runeSlot5.equals(0))
//                    temp = runeSlot5;
//                runeSlot5 = 0L;
//                break;
//            case 6:
//                if(!runeSlot6.equals(0))
//                    temp = runeSlot6;
//                runeSlot6 = 0L;
//                break;
//
//        }
//        return temp;
//    }
}
