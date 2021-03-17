package com.onlyonegames.eternalfantasia.domain.model.dto.Dungeon;

import java.util.List;

public class MyInfiniteTowerRewardReceivedInfosDto {

    public static class InfiniteTowerRewardReceivedInfo {
        public int floor;
        public boolean received;
    }
    public List<InfiniteTowerRewardReceivedInfo> infiniteTowerRewardReceivedInfos;
}
