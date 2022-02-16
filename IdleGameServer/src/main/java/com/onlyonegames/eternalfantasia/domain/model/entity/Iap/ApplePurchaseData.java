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
public class ApplePurchaseData extends BaseTimeEntity {
    @Id
    @TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String productId;
    String transactionId;
    String originalTransactionId;
    String purchaseDateMs;
    String expirationDate;
    boolean consume;

    public void Consume() {
        this.consume = true;
    };
}
