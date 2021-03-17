package com.onlyonegames.eternalfantasia.domain.model.dto.HotTimeEventDto;

import com.onlyonegames.eternalfantasia.domain.model.dto.FieldSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserFieldObjectInfoListDto;
import com.onlyonegames.util.MathHelper;

import java.util.List;

public class MyHotTimeInfoDto {
    /**필드 갯수 만큼 리스팅 14개*/
    public Long hotTimeSchedulerId;
    public UserFieldObjectInfoListDto userFieldObjectInfoListDto;

    public void SettingHotTimeInfo(List<HotTimeFieldObjectInfoListDto.HotTimeFieldObjectInfoList> hotTimeFieldObjectInfoList){
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
                switch(hotTimeFieldObjectInfoDto.gettingItem){
                    case "Gold":
                        userFieldObjectInfoDto.type = "Monster";
                        break;
                    case "Diamond":
                        userFieldObjectInfoDto.type = "Diamond";
                        break;
                    case "material":
                        userFieldObjectInfoDto.type = "Mine";
                        break;
                }
                for(int i = 0; i <hotTimeFieldObjectInfoDto.maxGenCount; i++){
                    UserFieldObjectInfoListDto.ObjectData objectData = new UserFieldObjectInfoListDto.ObjectData();
                    objectData.id = setId;
                    setId++;
                    objectData.isSpecial = false;
                    double temp = MathHelper.Range(1,101);
                    if(temp<hotTimeFieldObjectInfoDto.specialObjectGeneratePercent*100)
                        objectData.isSpecial = true;
                    objectData.state = 1;
                    userFieldObjectInfoDto.objectViewList.add(objectData);
                }
                userFieldObjectListInfoDto.userFieldObjectInfoDtoList.add(userFieldObjectInfoDto);
            }
            userFieldObjectInfoListDto.userFieldObjectListInfoDtoList.add(userFieldObjectListInfoDto);
        }
        this.userFieldObjectInfoListDto = userFieldObjectInfoListDto;
    }
}
