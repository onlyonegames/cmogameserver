package com.onlyonegames.eternalfantasia.domain.model.dto.MailDto;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailBox;
import lombok.Data;

@Data
public class MyMailBoxDto {
    Long id;
    Long useridUser;
    String json_myMailBoxInfo;

    public MyMailBox ToEntity() {
        return MyMailBox.builder().useridUser(useridUser).json_myMailBoxInfo(json_myMailBoxInfo).build();
    }
}
