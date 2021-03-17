package com.onlyonegames.eternalfantasia.domain.model.dto.GamePot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CouponWebHookDto {
    public String userId;
    public String projectId;
    public String platform;
    public String store;
    public String title;
    public String content;
    public String userData;
    public String itemId;
}
