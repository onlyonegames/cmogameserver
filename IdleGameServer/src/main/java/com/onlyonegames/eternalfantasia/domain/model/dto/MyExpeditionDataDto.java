package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyExpeditionData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyExpeditionDataDto {
    Long id;
    Long useridUser; //유저 아이디
    int boostCountForDay;//하루에 사용가능한 부스터수가 정해져있음.ExpeditionProcessTable.boostMaxCountPerDay에 정함.ExpeditionProcessTable.boostMaxCountPerDay 값이 0이면 무제한이다.
    int boostCompleteCount; //이번 탐색대에서 사용 완료한 부스터 카운트.
    boolean isUsingBoost; //현재 부스터를 사용중인지 체크
    int remainBoostTime; //이전 탐색에서 부스터를 사용중에 탐색 마치기를 해서 남은 부스터 시간.
    LocalDateTime boostStartTime;//부스터를 시작한 시간.ExpeditionProcessTable.boostTime 이 지나면 isUsingBoost를 false 해준다.
    LocalDateTime lastExpeditionFinishTime;//직전 탐색대 완료 시간이자 새로운 탐색대 시작시간. 해당 시간으로부터 ExpeditionProcessTable.maxFlowtime만큼 탐색 가능. 해당 시간이 지나면 회수하기 전까지 더이상 획득 할수 없음.
    LocalDateTime boostCountForDayClearTime;//boostCountForDay 클리어한 시간.(해당 유저 테이블이 만들어진 후부터 하루 간격으로 초기화 된다)

    public MyExpeditionData ToEntity() {
        return MyExpeditionData.builder().useridUser(useridUser).boostCountForDay(boostCountForDay).boostCompleteCount(boostCompleteCount).usingBoost(isUsingBoost)
                .remainBoostTime(remainBoostTime).boostStartTime(boostStartTime).lastExpeditionFinishTime(lastExpeditionFinishTime).boostCountForDayClearTime(boostCountForDayClearTime).build();
    }

    public void InitFromDbData(MyExpeditionData dbData) {
        this.id = dbData.getId();
        this.useridUser = dbData.getUseridUser();
        this.boostCountForDay = dbData.getBoostCountForDay();
        this.boostCompleteCount = dbData.getBoostCompleteCount();
        this.isUsingBoost = dbData.isUsingBoost();
        this.remainBoostTime = dbData.getRemainBoostTime();
        this.boostStartTime = dbData.getBoostStartTime();
        this.lastExpeditionFinishTime = dbData.getLastExpeditionFinishTime();
        this.boostCountForDayClearTime = dbData.getBoostCountForDayClearTime();
    }
}
