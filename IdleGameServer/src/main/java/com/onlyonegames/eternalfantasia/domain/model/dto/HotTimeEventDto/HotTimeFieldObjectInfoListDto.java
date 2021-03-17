package com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data


/**운영측에서 변경할 데이터*/
public class HotTimeFieldObjectInfoListDto {
    public static class HotTimeFieldObjectInfoDto {
        public boolean objectSetted;
        /*** 해당 필드에 오브젝트를 설정 했는지*/

        public int onceGenCount;
        /*** 한번에 나올수 있는 오브젝트 갯수*/
        public int maxGenCount;
        /*** 한번에 나올수 있는 오브젝트 갯수*/
        public float specialObjectGeneratePercent;
        /*** 각각 생성되는 오브젝트가 스페셜일 확률*/
        public float genCooltime;
        /*** 오브젝트 획득후 다음 오브젝트를 생성 시킬때까지 걸리는 시간*/
        public String gettingItem;
        /*** 오브젝트 획득시 얻게될 아이템*/
        public String type;/**오브젝트 타입 : Mine,Diamond,Monster*/
    }
    public static class HotTimeFieldObjectInfoList{
        public int field;
        /*** 필드 번호 1~14*/
        public List<HotTimeFieldObjectInfoDto> hotTimeFieldObjectInfoDtoList;
    }
    public List<HotTimeFieldObjectInfoList> hotTimeFieldObjectInfoList;
}