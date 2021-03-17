package com.onlyonegames.eternalfantasia.domain.model.entity.Coupon;

import com.onlyonegames.eternalfantasia.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class CouponInfo {
    @Id
    Long id;
    Long useridUser;
    String couponNo;
    String cuponTitle;
    String cuponContent;
    String couponPublisher;
    String gettingItemsInfo;
    int couponState;/*0 미사용, 1 웹훅콜, 2사용자에 의해 받아짐*/

    public void WebHook(Long useridUser) {
        this.useridUser = useridUser;
        this.couponState = 1;
    }
    public void ReceiveCupon() {
        this.couponState = 2;
    }
}
