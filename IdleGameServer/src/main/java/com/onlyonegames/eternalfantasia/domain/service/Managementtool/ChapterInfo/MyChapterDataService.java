package com.onlyonegames.eternalfantasia.domain.service.Managementtool.ChapterInfo;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto.ChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon.MyInfiniteTowerRewardReceivedInfosDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.MyChapterDataDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.Leaderboard.InfiniteRanking;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyHeroTowerExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyInfiniteTowerSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyOrdealDungeonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyChapterSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.*;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.Leaderboard.InfiniteRankingRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyChapterSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.Dungeon.MyInfiniteTowerPlayService;
import com.onlyonegames.eternalfantasia.domain.service.ErrorLoggingService;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
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
public class MyChapterDataService {
    private final MyChapterSaveDataRepository myChapterSaveDataRepository;
    private final MyHeroTowerExpandSaveDataRepository myHeroTowerExpandSaveDataRepository;
    private final MyOrdealDungeonExpandSaveDataRepository myOrdealDungeonExpandSaveDataRepository;
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;
    private final MyInfiniteTowerSaveDataRepository myInfiniteTowerSaveDataRepository;
    private final InfiniteRankingRepository infiniteRankingRepository;
    private final UserRepository userRepository;
    private final MyInfiniteTowerPlayService myInfiniteTowerPlayService;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> findUserChapterSaveData(Long userId, Map<String, Object> map){ //Json형식으로 변환하여 반환해야함으로 넘겨줄때 방식을 다른 방식으로 만들기
        MyChapterSaveData myChapterSaveData = myChapterSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myChapterSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyChapterSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyChapterSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyHeroTowerExpandSaveData myHeroTowerExpandSaveData = myHeroTowerExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myHeroTowerExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyHeroTowerExpandSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyHeroTowerExpandSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyAncientDragonExpandSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyInfiniteTowerSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyInfiniteTowerSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        ChapterSaveData chapterSaveData = JsonStringHerlper.ReadValueFromJson(myChapterSaveData.getSaveDataValue(), ChapterSaveData.class);
        ChapterSaveData myHeroTowerSaveData = JsonStringHerlper.ReadValueFromJson(myHeroTowerExpandSaveData.getJson_saveDataValue(), ChapterSaveData.class);
        ChapterSaveData myOrdealDungeonSaveData = JsonStringHerlper.ReadValueFromJson(myOrdealDungeonExpandSaveData.getJson_saveDataValue(), ChapterSaveData.class);
        ChapterSaveData myAncientDragonSaveData = JsonStringHerlper.ReadValueFromJson(myAncientDragonExpandSaveData.getJson_saveDataValue(), ChapterSaveData.class);
        MyInfiniteTowerRewardReceivedInfosDto myInfiniteTowerRewardReceivedInfosDto = JsonStringHerlper.ReadValueFromJson(myInfiniteTowerSaveData.getReceivedRewardInfoJson(), MyInfiniteTowerRewardReceivedInfosDto.class);
        List<MyChapterDataDto.StageInfo> stageInfoList = new ArrayList<>();
        List<MyChapterDataDto.TowerInfo> towerInfoList = new ArrayList<>();
        List<MyChapterDataDto.DungeonInfo> dungeonInfoList = new ArrayList<>();
        List<MyChapterDataDto.DragonInfo> dragonInfoList = new ArrayList<>();
        List<MyChapterDataDto.RewardReceivedInfo> rewardReceivedInfoList = new ArrayList<>();
        MyChapterDataDto myChapterDataDto = new MyChapterDataDto();
        for(ChapterSaveData.ChapterPlayInfo chapterPlayInfo:chapterSaveData.chapterData.chapterPlayInfosList){
            int chapterNo = chapterPlayInfo.chapterNo;
            for(ChapterSaveData.StagePlayInfo stagePlayInfo:chapterPlayInfo.stagePlayInfosList){
                MyChapterDataDto.StageInfo stageInfo = new MyChapterDataDto.StageInfo();
                String star = new String();
                if(stagePlayInfo.isGettedStar3){
                    star = "3";
                } else if(stagePlayInfo.isGettedStar2){
                    star = "2";
                } else if(stagePlayInfo.isGettedStar1){
                    star = "1";
                } else{
                    star = "X";
                }
                stageInfo.setStageInfo("Stage_"+Integer.toString(chapterNo)+"_"+Integer.toString(stagePlayInfo.stageNo),
                        stagePlayInfo.isOpend, star);
                stageInfoList.add(stageInfo);
            }
        }
        for(ChapterSaveData.ChapterPlayInfo towerPlayInfo:myHeroTowerSaveData.chapterData.chapterPlayInfosList){
            for(ChapterSaveData.StagePlayInfo stagePlayInfo:towerPlayInfo.stagePlayInfosList){
                String star = new String();
                if(stagePlayInfo.isGettedStar3){
                    star = "3";
                } else if(stagePlayInfo.isGettedStar2){
                    star = "2";
                } else if(stagePlayInfo.isGettedStar1){
                    star = "1";
                } else{
                    star = "X";
                }
                MyChapterDataDto.TowerInfo towerInfo = new MyChapterDataDto.TowerInfo();
                towerInfo.setTowerInfo("Tower_"+Integer.toString(stagePlayInfo.stageNo),stagePlayInfo.isOpend, star);
                towerInfoList.add(towerInfo);
            }
        }
        for(ChapterSaveData.ChapterPlayInfo dungeonPlayInfo:myOrdealDungeonSaveData.chapterData.chapterPlayInfosList){
            for(ChapterSaveData.StagePlayInfo stagePlayInfo:dungeonPlayInfo.stagePlayInfosList){
                String star = new String();
                if(stagePlayInfo.isGettedStar3){
                    star = "3";
                } else if(stagePlayInfo.isGettedStar2){
                    star = "2";
                } else if(stagePlayInfo.isGettedStar1){
                    star = "1";
                } else{
                    star = "X";
                }
                MyChapterDataDto.DungeonInfo dungeonInfo = new MyChapterDataDto.DungeonInfo();
                dungeonInfo.setDungeonInfo("Dungeon_"+Integer.toString(stagePlayInfo.stageNo),stagePlayInfo.isOpend, star);
                dungeonInfoList.add(dungeonInfo);
            }
        }
        for(ChapterSaveData.ChapterPlayInfo dragonPlayInfo:myAncientDragonSaveData.chapterData.chapterPlayInfosList){
            for(ChapterSaveData.StagePlayInfo stagePlayInfo:dragonPlayInfo.stagePlayInfosList){
                String star = new String();
                if(stagePlayInfo.isGettedStar3){
                    star = "3";
                } else if(stagePlayInfo.isGettedStar2){
                    star = "2";
                } else if(stagePlayInfo.isGettedStar1){
                    star = "1";
                } else{
                    star = "X";
                }
                MyChapterDataDto.DragonInfo dragonInfo = new MyChapterDataDto.DragonInfo();
                dragonInfo.setDragonInfo("Dragon_"+Integer.toString(stagePlayInfo.stageNo),stagePlayInfo.isOpend, star);
                dragonInfoList.add(dragonInfo);
            }
        }
        for(MyInfiniteTowerRewardReceivedInfosDto.InfiniteTowerRewardReceivedInfo infiniteTowerRewardReceivedInfo:myInfiniteTowerRewardReceivedInfosDto.infiniteTowerRewardReceivedInfos){
            MyChapterDataDto.RewardReceivedInfo rewardReceivedInfo = new MyChapterDataDto.RewardReceivedInfo();
            rewardReceivedInfo.setRewardReceivedInfo(infiniteTowerRewardReceivedInfo.floor, infiniteTowerRewardReceivedInfo.received);
            rewardReceivedInfoList.add(rewardReceivedInfo);
        }
        myChapterDataDto.setMyChapterData(stageInfoList, towerInfoList, myOrdealDungeonExpandSaveData.getBonusRemainCount(), dungeonInfoList, myAncientDragonExpandSaveData.getPlayableRemainCount(), dragonInfoList, rewardReceivedInfoList, myInfiniteTowerSaveData.getArrivedTopFloor());
        map.put("MyChapterDataDto", myChapterDataDto);
        return map;
    }

    public Map<String, Object> ResetDungeonBonus(Long userId, Map<String, Object> map) {
        MyOrdealDungeonExpandSaveData myOrdealDungeonExpandSaveData = myOrdealDungeonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myOrdealDungeonExpandSaveData == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myOrdealDungeonExpandSaveData.ResetBonus();
        map.put("MyOrdealDungeonExpandSaveData", myOrdealDungeonExpandSaveData);
        return map;
    }

    public Map<String, Object> ResetPlayable(Long userId, Map<String, Object> map) {
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myAncientDragonExpandSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyOrdealDungeonExpandSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        myAncientDragonExpandSaveData.ResetPlayable();;
        map.put("MyAncientDragonExpandSaveData", myAncientDragonExpandSaveData);
        return map;
    }

    public Map<String, Object> SetInfiniteTower(Long userId, int setFloor, Map<String, Object> map) {
        MyInfiniteTowerSaveData myInfiniteTowerSaveData = myInfiniteTowerSaveDataRepository.findByUseridUser(userId)
                .orElse(null);
        if(myInfiniteTowerSaveData == null) {
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: MyInfiniteTowerSaveData not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: MyInfiniteTowerSaveData not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            errorLoggingService.SetErrorLog(userId, ResponseErrorCode.NOT_FIND_DATA.getIntegerValue(), "[ManagementTool] Fail! -> Cause: User not find userId.", this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), IS_DIRECT_WRIGHDB);
            throw new MyCustomException("Fail! -> Cause: User not find userId.", ResponseErrorCode.NOT_FIND_DATA);
        }

        InfiniteRanking infiniteRanking = infiniteRankingRepository.findByUseridUser(userId).orElse(null);

        for(int i = myInfiniteTowerSaveData.getArrivedTopFloor();i<setFloor-1;i++){
            if(i%25==0)
                infiniteRanking = myInfiniteTowerPlayService.setScore(user, infiniteRanking, i);
        }
        infiniteRanking = myInfiniteTowerPlayService.setScore(user, infiniteRanking, setFloor-1);
        myInfiniteTowerSaveData.SetArrivedTopFloor(setFloor);
        map.put("arrivedFloor", myInfiniteTowerSaveData.getArrivedTopFloor());
        return map;
    }
}
