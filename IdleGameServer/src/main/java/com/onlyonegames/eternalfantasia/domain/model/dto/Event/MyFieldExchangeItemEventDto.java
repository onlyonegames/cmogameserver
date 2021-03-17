package com.onlyonegames.eternalfantasia.domain.model.dto.Event;

import com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto.HotTimeFieldObjectInfoListDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserFieldObjectInfoListDto;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.ExchangeItemEventTable;
import com.onlyonegames.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class MyFieldExchangeItemEventDto {
    public Long eventId;
    public List<MaxExchange> maxExchangeList;
    public UserFieldObjectInfoListDto userFieldExchangeItemEventDto;

    public void SettingMyFieldExchangeItemEvent(List<HotTimeFieldObjectInfoListDto.HotTimeFieldObjectInfoList> hotTimeFieldObjectInfoList, List<ExchangeItemEventTable> exchangeItemEventTableList){
        UserFieldObjectInfoListDto userFieldObjectInfoListDto = new UserFieldObjectInfoListDto();
        int setId = 1;
        for(HotTimeFieldObjectInfoListDto.HotTimeFieldObjectInfoList hotTimeFieldObjectInfo : hotTimeFieldObjectInfoList){
            UserFieldObjectInfoListDto.UserFieldObjectListInfoDto userFieldObjectListInfoDto = new UserFieldObjectInfoListDto.UserFieldObjectListInfoDto();
            userFieldObjectListInfoDto.field = hotTimeFieldObjectInfo.field;
            for(HotTimeFieldObjectInfoListDto.HotTimeFieldObjectInfoDto hotTimeFieldObjectInfoDto : hotTimeFieldObjectInfo.hotTimeFieldObjectInfoDtoList){
                UserFieldObjectInfoListDto.UserFieldObjectInfoDto userFieldObjectInfoDto = new UserFieldObjectInfoListDto.UserFieldObjectInfoDto();
                userFieldObjectInfoDto.maxGenCount = hotTimeFieldObjectInfoDto.maxGenCount;
                userFieldObjectInfoDto.onceGenCount = hotTimeFieldObjectInfoDto.onceGenCount;
                userFieldObjectInfoDto.genCooltime = hotTimeFieldObjectInfoDto.genCooltime;
                userFieldObjectInfoDto.gettingItem = hotTimeFieldObjectInfoDto.gettingItem;
                userFieldObjectInfoDto.type = "ExchangeItem"; // 추후 자세한 내용이 나오면 Type 추가 필요
                for(int i = 0; i <hotTimeFieldObjectInfoDto.maxGenCount; i++){
                    UserFieldObjectInfoListDto.ObjectData objectData = new UserFieldObjectInfoListDto.ObjectData();
                    objectData.id = setId;
                    setId++;
                    objectData.isSpecial = false;
                    objectData.state = 1;
                    userFieldObjectInfoDto.objectViewList.add(objectData);
                }
                userFieldObjectListInfoDto.userFieldObjectInfoDtoList.add(userFieldObjectInfoDto);
            }
            userFieldObjectInfoListDto.userFieldObjectListInfoDtoList.add(userFieldObjectListInfoDto);
        }
        List<MaxExchange> maxExchangeList = new ArrayList<>();
        for(ExchangeItemEventTable temp : exchangeItemEventTableList){
            MaxExchange maxExchange = new MaxExchange();
            maxExchange.SetMaxExchange(temp.getId(), temp.getMaxExchange());
            maxExchangeList.add(maxExchange);
        }
        this.maxExchangeList = maxExchangeList;
        this.userFieldExchangeItemEventDto = userFieldObjectInfoListDto;
    }

    public static class MaxExchange{
        public int itemId;
        public int exchangeableCount;

        public void SetMaxExchange(int itemId, int exchangeableCount){
            this.itemId = itemId;
            this.exchangeableCount = exchangeableCount;
        }

        public boolean ExchangeItem() {
            if(exchangeableCount>0){
                exchangeableCount -= 1;
                return true;
            }
            return false;
        }
    }
}
