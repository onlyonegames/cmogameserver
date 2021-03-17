package com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool;

import lombok.Data;

import java.util.List;

@Data
public class MyChapterDataDto {
    public List<StageInfo> stageInfoList;
    public List<TowerInfo> towerInfoList;
    public int bonusCount;
    public List<DungeonInfo> dungeonInfoList;
    public int playableCount;
    public List<DragonInfo> dragonInfoList;
    public int arrivedTopFloor;
    public List<RewardReceivedInfo> rewardReceivedInfoList;

    public static class TowerInfo{
        public String towerName;
        public boolean openInfo;
        public String starNum;

        public void setTowerInfo(String towerName, boolean openInfo, String starNum){
            this.towerName = towerName;
            this.openInfo = openInfo;
            this.starNum = starNum;
        }
    }

    public static class DungeonInfo{
        public String dungeonName;
        public boolean openInfo;
        public String starNum;

        public void setDungeonInfo(String dungeonName, boolean openInfo, String starNum){
            this.dungeonName = dungeonName;
            this.openInfo = openInfo;
            this.starNum = starNum;
        }
    }

    public static class DragonInfo{
        public String dungeonName;
        public boolean openInfo;
        public String starNum;

        public void setDragonInfo(String dungeonName, boolean openInfo, String starNum){
            this.dungeonName = dungeonName;
            this.openInfo = openInfo;
            this.starNum = starNum;
        }
    }


    public static class StageInfo{
        public String stageName;
        public boolean openInfo;
        public String starNum;

        public void setStageInfo(String stageName, boolean openInfo, String starNum){
            this.stageName = stageName;
            this.openInfo = openInfo;
            this.starNum = starNum;
        }
    }




    public static class RewardReceivedInfo{
        public int floor;
        public boolean received;

        public void setRewardReceivedInfo(int floor, boolean received){
            this.floor = floor;
            this.received = received;
        }
    }

    public void setMyChapterData(List<StageInfo> stageInfoList,
                                 List<TowerInfo> towerInfoList,
                                 int bonusCount,
                                 List<DungeonInfo> dungeonInfoList,
                                 int playableCount,
                                 List<DragonInfo> dragonInfoList,
                                 List<RewardReceivedInfo> rewardReceivedInfoList,
                                 int arrivedTopFloor){
        this.stageInfoList = stageInfoList;
        this.towerInfoList = towerInfoList;
        this.bonusCount = bonusCount;
        this.dungeonInfoList = dungeonInfoList;
        this.playableCount = playableCount;
        this.dragonInfoList = dragonInfoList;
        this.rewardReceivedInfoList = rewardReceivedInfoList;
        this.arrivedTopFloor = arrivedTopFloor;
    }
}
