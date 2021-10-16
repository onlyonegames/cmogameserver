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
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
