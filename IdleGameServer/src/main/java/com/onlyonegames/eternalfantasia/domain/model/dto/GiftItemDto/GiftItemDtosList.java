package com.onlyonegames.eternalfantasia.domain.model.dto.GiftItemDto;

import java.util.List;

public class GiftItemDtosList {
    public static class GiftItemDto
    {
        public String code;
        public int count;

        public GiftItemDto() {

        }

        public GiftItemDto(String code, int count) {
            this.code = code;
            this.count = count;
        }
        public boolean SpendItem(int discount) {
            if(count < discount)
                return false;
            count -= discount;
            return true;
        }
        public void AddItem(int addCount, int stackLimit) {
            count += addCount;
            if(count > stackLimit)
                count = stackLimit;
        }
    }
    public List<GiftItemDto> giftItemDtoList;
}
