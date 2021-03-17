package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.HeroTowerExpandData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyOrdealDungeonExpandSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyOrdealDungeonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.service.Companion.MyGiftInventoryService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import com.onlyonegames.util.MathHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onlyonegames.eternalfantasia.EternalfantasiaApplication.IS_DIRECT_WRIGHDB;

@Service
@Transactional
@AllArgsConstructor
public class MyOrdealDungeonSeasonService {
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetSeasonInfo(Long userId, Map<String, Object> map) {
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myOrdealDungeonExpandSaveData.IsResetSeasonStartTime()) {
            String json_saveDataValue = myOrdealDungeonExpandSaveData.getJson_saveDataValue();
            ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ChapterSaveData.class);
            ChapterSaveData.ChapterPlayInfo chapterPlayInfo  = chapterSaveData.chapterData.chapterPlayInfosList.get(0);
            for(ChapterSaveData.StagePlayInfo stagePlayInfo : chapterPlayInfo.stagePlayInfosList) {
                stagePlayInfo.isCleared = false;
                stagePlayInfo.isGettedStar1 = false;
                stagePlayInfo.isGettedStar2 = false;
                stagePlayInfo.isGettedStar3 = false;
            }
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData);
            myOrdealDungeonExpandSaveData.ResetSaveDataValue(json_saveDataValue);
        }
        MyOrdealDungeonExpandSaveDataDto myOrdealDungeonExpandSaveDataDto = new MyOrdealDungeonExpandSaveDataDto();
        myOrdealDungeonExpandSaveDataDto.InitFromDbData(myOrdealDungeonExpandSaveData);
        map.put("myOrdealDungeonExpandSaveData", myOrdealDungeonExpandSaveDataDto);
        return map;
    }
}

