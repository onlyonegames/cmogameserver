package com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class MyFieldDungeonSeasonSaveData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    Long seasonRank;
    Long totalDamage;
    int seasonNo;
    boolean seasonReceived; //사용 여부 재검토 필요
    int purchaseNum;

    public void Received() {
        this.seasonReceived = true;
    } //사용 여부 재검토 필요

    public void ResetSeasonSaveData(){
        this.totalDamage = 0L;
        this.seasonRank = 0L;
    }

    public void ResetSeasonNo(int seasonNo) {
        this.seasonNo = seasonNo;
    }

    public void Finish(Long seasonRank, Long totalDamage) {
        this.seasonRank = seasonRank;
        this.totalDamage = totalDamage;
    }

    public void AddPurchasableNum() {
        purchaseNum += 1;
    }

    public void ResetPurchaseNum(){
        this.purchaseNum = 0;
    }

    public void setSeasonRank(Long seasonRank) {
        this.seasonRank = seasonRank;
    }
}
