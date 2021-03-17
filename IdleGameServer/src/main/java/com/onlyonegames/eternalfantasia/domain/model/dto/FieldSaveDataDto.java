package com.onlyonegames.eternalfantasia.domain.model.dto;

/**
 * 확률 추가 : 특수 개체가 나올수 있는 확률 : // Service에서 확률계산 후 Bool로 Dto에 넘겨줌? //클라이언트에서 생성한뒤 gettingObject호출시 특수 개체임을 알리는 +a가 있음? // server에서는 필드 object 생성하는 api가 없음
 * 핫타임 이벤트 : 운영자가 특정 api 호출하면 핫타임 이벤트를 설정하면 설정된 시간부터 이벤트가 적용된다. //
 * 핫타임 이벤트는 기존에 필드에 생성되는 오브젝트들의 갯수가 시스템에 셋팅 된 숫자로 늘어나게 된다. //필드 Max값과 Max값이 같이 증가?
 *
 * 예외처리 추가 : 금일 획득한 오브젝트가 Max 인상황에서 클라이언트에 gettingObject 요청이 들어오면 예외처리한다. //생성자체가 불가능 하게 만들어 필드에 Object가 없게?
 *
 * //gettingMax 하루에 최대 얻을 수 있는 수?
 * 2020-09-03 by rainful FieldSaveDataDto 내부에 있던 inner class 를 관리 차원에서 따로 뺌
 * 그러면서 FieldSaveData라는 내부 클래스의 것들을 FieldSaveDataDto 로 빼고 해당 클래스와 변수는 삭제함.
 */
public class FieldSaveDataDto {
    public boolean changeableSkywalker;//비공정탈수있는지.챕터를 다깨야 비공정 탑승 가능.
    public int equipSkywalkerId;//사용중인 비공정. 추후 다양한 비공정 추가 가능.0번은 영웅
    public int plaingFieldNo;//현재 유저가 선택해서 플레이중인 챕터(=FieldNo)
    public boolean modeLevel; // 2020-04-06 : 현재 선택한 난이도 -노멀인지 하드인지 - 체크
    public boolean hardModeOpenable;
    public UserFieldObjectInfoListDto userFieldObjectInfoListDto;

    public void SetEquipSkywalkerId(int equipSkywalkerId) {
        this.equipSkywalkerId = equipSkywalkerId;
    }
    public void SetPlaingFieldNo(int plaingFieldNo){
        this.plaingFieldNo = plaingFieldNo;
    }
}
