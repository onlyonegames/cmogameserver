package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyAncientDragonExpandSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyAncientDragonSeasonService {
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetSeasonInfo(Long userId, Map<String, Object> map) {
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyAncientDragonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }

        if(myAncientDragonExpandSaveData.IsResetSeasonStartTime()) {
            String json_saveDataValue = myAncientDragonExpandSaveData.getJson_saveDataValue();
            ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ChapterSaveData.class);
            ChapterSaveData.ChapterPlayInfo chapterPlayInfo  = chapterSaveData.chapterData.chapterPlayInfosList.get(0);
            for(ChapterSaveData.StagePlayInfo stagePlayInfo : chapterPlayInfo.stagePlayInfosList) {
                stagePlayInfo.isCleared = false;
                stagePlayInfo.isGettedStar1 = false;
                stagePlayInfo.isGettedStar2 = false;
                stagePlayInfo.isGettedStar3 = false;
            }
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData);
            myAncientDragonExpandSaveData.ResetSaveDataValue(json_saveDataValue);
        }
        MyAncientDragonExpandSaveDataDto myAncientDragonExpandSaveDataDto = new MyAncientDragonExpandSaveDataDto();
        myAncientDragonExpandSaveDataDto.InitFromDbData(myAncientDragonExpandSaveData);
        map.put("myAncientDragonExpandSaveData", myAncientDragonExpandSaveDataDto);
        return map;
    }
}
