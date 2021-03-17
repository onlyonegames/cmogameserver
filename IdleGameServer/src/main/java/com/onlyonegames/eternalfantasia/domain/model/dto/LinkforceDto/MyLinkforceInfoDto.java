package com.onlyonegames.eternalfantasia.domain.model.dto.LinkforceDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Companion.MyLinkforceInfo;
import lombok.Data;

@Data
public class MyLinkforceInfoDto {
    Long id;
    Long useridUser;
    String json_LinkforceInfos;

    public MyLinkforceInfo ToEntity() {
        return MyLinkforceInfo.builder().useridUser(useridUser).json_LinkforceInfos(json_LinkforceInfos).build();
    }

    public void InitFromDbData(MyLinkforceInfo dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.json_LinkforceInfos = dbData.getJson_LinkforceInfos();
    }
}
