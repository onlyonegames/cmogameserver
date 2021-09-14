package com.onlyonegames.eternalfantasia.domain.model.entity.Iap;


import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class GooglePurchaseData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String goodsId;
    String transactionID;
    String signedData;
    String signature;
    String orderId;
    boolean consume;

    public void Consume() {
        this.consume = true;
    }
}
