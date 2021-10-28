package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Logging.CouponUsedLog;
import lombok.Data;

@Data
public class CouponUsedLogDto {
    Long id;
    Long useridUser;
    Long usedCouponId;
    String usedCouponCode;

    public CouponUsedLog ToEntity() {
        return CouponUsedLog.builder().useridUser(useridUser).usedCouponId(usedCouponId).usedCouponCode(usedCouponCode).build();
    }

    public void SetCouponUsedLogDto(Long userId, Long usedCouponId, String usedCouponCode) {
        this.useridUser = userId;
        this.usedCouponId = usedCouponId;
        this.usedCouponCode = usedCouponCode;
    }
}
