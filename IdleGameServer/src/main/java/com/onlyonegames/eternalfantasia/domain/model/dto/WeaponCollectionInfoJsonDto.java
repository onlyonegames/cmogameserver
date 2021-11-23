package com.onlyonegames.eternalfantasia.domain.model.dto;

import java.util.ArrayList;
import java.util.List;

public class WeaponCollectionInfoJsonDto {
    public List<Collection> buffer;

    public static class Collection {
        public int level;
        public int exp;
        public List<Boolean> isGetRewards;

        public void SetIsGetRewards() {
            this.isGetRewards = new ArrayList<>();
            for (int i = 0; i<5; i++) {
                this.isGetRewards.add(false);
            }
        }
    }
}
