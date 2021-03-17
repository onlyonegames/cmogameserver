package com.onlyonegames.eternalfantasia.domain.model.dto.ResponseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyMailBoxResponseDto {
    Long mailId;
    String from;
    String title;
    String content;
    String gettingItems;
    String gettingItemCounts;
    LocalDateTime sendDate;//우편 보낸 날자
    LocalDateTime expireDate;//우편 만료 날자
    int mailType;/*0 only messeage, 1 아이템 선물*/
    boolean hasRead;
    boolean received;
}
