package com.onlyonegames.eternalfantasia.domain.service.Dungeon;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.HeroTowerExpandData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyHeroTowerExpandSaveDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.GiftTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyHeroTowerExpandSaveDataRepository;
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
public class MyHeroTowerSeasonService {
    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final GameDataTableService gameDataTableService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> GetSeasonInfo(Long userId, Map<String, Object> map) {
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myHeroTowerExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "Fail! -> Cause: MyHeroTowerExpandSaveData not find.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA);
        }
        if(myHeroTowerExpandSaveData.IsResetSeasonStartTime()) {
            String json_saveDataValue = myHeroTowerExpandSaveData.getJson_saveDataValue();
            ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(json_saveDataValue, ChapterSaveData.class);
            ChapterSaveData.ChapterPlayInfo chapterPlayInfo  = chapterSaveData.chapterData.chapterPlayInfosList.get(0);
            for(ChapterSaveData.StagePlayInfo stagePlayInfo : chapterPlayInfo.stagePlayInfosList) {

                stagePlayInfo.isCleared = false;
                stagePlayInfo.isGettedStar1 = false;
                stagePlayInfo.isGettedStar2 = false;
                stagePlayInfo.isGettedStar3 = false;
            }
            json_saveDataValue = JsonStringHerlper.WriteValueAsStringFromData(chapterSaveData);
            myHeroTowerExpandSaveData.ResetSaveDataValue(json_saveDataValue);

            /*각층 영웅 조각 및 해당 영웅이 제일 좋아하는 선물 셋팅*/
            String json_ExpandInfo = myHeroTowerExpandSaveData.getJson_ExpandInfo();
            HeroTowerExpandData heroTowerExpandData = JsonStringHerlper.ReadValueFromJson(json_ExpandInfo, HeroTowerExpandData.class);
            heroTowerExpandData.heroTowerFloorDataList.clear();
            List<String> tier2HeroCodesList = new ArrayList<>();
            List<String> tier3HeroCodesList = new ArrayList<>();

           // List<String> heroCodesList = new ArrayList<>();
            List<herostable> herosTablesList = gameDataTableService.HerosTableList();
            for(herostable heroInfo : herosTablesList) {
                String heroCode = heroInfo.getCode();
                if(heroCode.equals("hero"))
                    continue;
                int tier = heroInfo.getTier();
                if(tier == 2)
                    tier2HeroCodesList.add(heroInfo.getCode());
                else if(tier == 3)
                    tier3HeroCodesList.add(heroInfo.getCode());
            }

            /*1층부터 10층 셋팅*/
           // List<GiftTable> giftTableList = gameDataTableService.GiftsTableList();
            for(int i = 0; i < 10; i++) {
                int selectedTier2RandomIndex = (int)MathHelper.Range(0, tier2HeroCodesList.size());
                int selectedTier3RandomIndex = (int)MathHelper.Range(0, tier3HeroCodesList.size());

                String selectedTier2RandomHeroCode = tier2HeroCodesList.get(selectedTier2RandomIndex);
                String selectedTier3RandomHeroCode = tier3HeroCodesList.get(selectedTier3RandomIndex);

                HeroTowerExpandData.HeroTowerFloorData heroTowerFloorData = new HeroTowerExpandData.HeroTowerFloorData();
                heroTowerFloorData.rewardCharacterPieceCodeTier2 = selectedTier2RandomHeroCode;
                heroTowerFloorData.rewardCharacterPieceCodeTier3 = selectedTier3RandomHeroCode;
                heroTowerExpandData.heroTowerFloorDataList.add(heroTowerFloorData);
            }
            json_ExpandInfo = JsonStringHerlper.WriteValueAsStringFromData(heroTowerExpandData);
            myHeroTowerExpandSaveData.ResetExpandInfo(json_ExpandInfo);
        }
        MyHeroTowerExpandSaveDataDto myHeroTowerExpandSaveDataDto = new MyHeroTowerExpandSaveDataDto();
        myHeroTowerExpandSaveDataDto.InitFromDbData(myHeroTowerExpandSaveData);
        map.put("myHeroTowerExpandSaveData", myHeroTowerExpandSaveDataDto);
        return map;
    }
}
