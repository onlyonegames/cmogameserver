package com.onlyonegames.eternalfantasia.domain.model.entity.Inventory;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MyProductionSlot extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long userIdUser;
    int slotNo;/*유저별 슬롯 번호, UI에서는 해당 정보 오름차순으로 Draw 한다.*/
    int itemId;/*제작 완료시 Inventory에 생성될 장비의 ID.*/
    int state;/*제작 상태. 0 비어있음, 1 제작중 */
    int reduceSecondFromAD;/*광고 보기를 통해 줄인 시간(초로 환산). 광고는 동일한 제작템에 대해 한번만 가능 하다.*/
    LocalDateTime productionStartTime;/*제작 시작 시간*/
    @Builder
    public MyProductionSlot(Long userIdUser, int slotNo, int itemId, int state, int reduceSecondFromAD, LocalDateTime productionStartTime) {
        this.userIdUser = userIdUser;
        this.slotNo = slotNo;
        this.itemId = itemId;
        this.state = state;
        this.reduceSecondFromAD = reduceSecondFromAD;
        this.productionStartTime = productionStartTime;
    }

    public boolean StartProduction(int itemId) {
        if(state != 0)
            return false;
        this.itemId = itemId;
        this.productionStartTime = LocalDateTime.now();
        this.state = 1;
        this.reduceSecondFromAD = 0;
        return true;
    }

    public boolean ReturnProduction(int productionSecond) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(productionStartTime, now);
        long totalTime = duration.getSeconds();
        if(totalTime >= productionSecond - reduceSecondFromAD) {
            this.state = 0;
            return true;
        }
        return false;
    }
    /*reduceSecondFromAD 가 0 이 아니면 이미 광고를 본적이 있음.*/
    public boolean ReduceProductionTime(int reduceSecond) {
        if(reduceSecondFromAD != 0) {
            return false;
        }
        this.reduceSecondFromAD = reduceSecond;
        return true;
    }

    public void ReturnProductionImmediately() {
        this.state = 0;
    }

    public long RemainProductionSecond(int productionSecond) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(productionStartTime, now);
        long remainSecond = productionSecond - duration.getSeconds() - reduceSecondFromAD;
        if(remainSecond < 0)
            remainSecond = 0;
        return remainSecond;
    }
}
