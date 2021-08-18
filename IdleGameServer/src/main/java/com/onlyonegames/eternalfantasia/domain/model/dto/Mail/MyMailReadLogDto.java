package com.onlyonegames.eternalfantasia.domain.model.dto.Mail;

import com.onlyonegames.eternalfantasia.domain.model.entity.Mail.MyMailReadLog;
import lombok.Data;

@Data
public class MyMailReadLogDto {
    Long id;
    Long useridUser;
    Long readMailId;

    public MyMailReadLog ToEntity() {
        return MyMailReadLog.builder().useridUser(useridUser).readMailId(readMailId).build();
    }
}
