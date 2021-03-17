package com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceWeaponDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkweaponInfo;
import lombok.Data;

@Data
public class MyLinkweaponInfoDto {
    Long id;
    Long useridUser;
    String json_LinkweaponRevolution;

    public MyLinkweaponInfo ToEntity() {
        return MyLinkweaponInfo.builder().useridUser(useridUser).json_LinkweaponRevolution(json_LinkweaponRevolution).build();
    }
}
