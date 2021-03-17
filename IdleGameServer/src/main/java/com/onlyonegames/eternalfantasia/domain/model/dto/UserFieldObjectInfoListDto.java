package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.util.MathHelper;

import java.util.ArrayList;
import java.util.List;
/*2020-09-03 by rainful FieldSaveDataDto 내부에 있던 inner class 를 관리 차원에서 따로 뺌*/
public class UserFieldObjectInfoListDto {
    /**오브젝트가 필드에 생성 될때 필요한 정보, 오브젝트를 클라이언트가 획득 할때 필요한 정보*/
    public static class ObjectData {
        public int id;/**필드 각 개체별 아이디*/
        public boolean isSpecial;/**해당 개체가 스페셜 개체인지 판단하기 위한 변수*/
        public int state;/**1은 아직 겟 안함. 2는 겟 완료!*/
    }
    /**해당 타입의 오브젝트에 생성 정보*/
    public static class UserFieldObjectInfoDto {
        public int maxGenCount;
        public int onceGenCount;/**한번에 나올수 있는 오브젝트 갯수*/
        public float genCooltime;/**오브젝트 획득후 다음 오브젝트를 생성 시킬때까지 걸리는 시간*/
        public String gettingItem;/**오브젝트 획득시 얻게될 아이템*/
        public String type;/**오브젝트 타입 : Mine,Diamond,Monster*/
        public List<UserFieldObjectInfoListDto.ObjectData> objectViewList = new ArrayList<>();
    }
    /**필드에 나타나는 오브젝트 타입의 종류는 총 3가지*/
    public static class UserFieldObjectListInfoDto {
        public int field;/**필드 번호 1~14*/
        public List<UserFieldObjectInfoDto> userFieldObjectInfoDtoList = new ArrayList<>(3);
    }
    /**필드 갯수 만큼 리스팅 14개*/
    public List<UserFieldObjectListInfoDto> userFieldObjectListInfoDtoList = new ArrayList<>();
    /*2020-09-02 by rainful 핫타임 오브젝트 클리어 추가*/
    public void Reset(double specialPercent) {
        for (UserFieldObjectListInfoDto userFieldObjectListInfoDto : userFieldObjectListInfoDtoList) {
            for (UserFieldObjectInfoDto userFieldObjectInfoDto : userFieldObjectListInfoDto.userFieldObjectInfoDtoList) {
                for (ObjectData objectData : userFieldObjectInfoDto.objectViewList){
                    objectData.isSpecial = false;
                    double temp = MathHelper.Range(1, 101);
                    if (temp < (specialPercent * 100)) {
                        objectData.isSpecial = true;
                    }
                    objectData.state = 1;
                }
            }
        }
    }
}
