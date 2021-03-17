package com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto;

import lombok.Data;

@Data
public class ItemRequestDto {
    //테이블내 UID
    Long id;
    //해당 아이템 코드
    //코드가 필요한 이유: 각 아이템 종류에 따라 저장되는 테이블이 다르므로 code 값으로 어떤 테이블을 조회 할지 결정하고
    //해당 테이블로부터 id를 Key로 하여 아이템 상태를 조회한다.
    String code;
    int count;//강화석 혹은 재련석의 경우 동일한 종류를 1개 이상 보낼수 있음.
}
