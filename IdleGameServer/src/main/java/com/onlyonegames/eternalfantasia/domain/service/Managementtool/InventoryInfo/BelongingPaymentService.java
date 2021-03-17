package com.onlyonegames.eternalfantasia.domain.service.Managementtool.InventoryInfo;

import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Managementtool.BelongingPaymentDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class BelongingPaymentService {
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final ItemTypeRepository itemTypeRepository;
    public Map<String, Object> addBelonging(BelongingPaymentDto dto, Map<String, Object> map){
        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(dto.getUseridUser());
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
        int limit = 0;
        if(dto.itemType == 2)
            limit = 1;
        else
            limit = 99999;
        int flag = 0;
        for(BelongingInventory belonging: belongingInventoryList){
            if(dto.getItemId() == belonging.getItemId() && getItemType(dto.getItemType(), itemTypeList).equals(belonging.getItemType())){
                belonging.AddItem(setZero(dto.getCount(),belonging.getCount()), limit);
                belonging = belongingInventoryRepository.save(belonging);
                flag = 1;
                map.put("BelongingInventory", belonging);
                break;
            }

        }
        if(flag != 1) {
            belongingInventoryDto.setUseridUser(dto.getUseridUser());
            belongingInventoryDto.setItemId(dto.getItemId());
            belongingInventoryDto.setCount(setLimitCount(dto.getCount(), limit));
            belongingInventoryDto.setItemType(getItemType(dto.getItemType(), itemTypeList));
            BelongingInventory belongingInventory = belongingInventoryDto.ToEntity();
            belongingInventory = belongingInventoryRepository.save(belongingInventory);
            map.put("BelongingInventory", belongingInventory);
        }
        return map;
    }
    int setZero(int count, int nowCount){
        if(nowCount + count <= 0){
            return -nowCount;
        }
        else return count;
    }

    int setLimitCount(int count, int limit){
        if(count > limit)
            return limit;
        else
            return count;
    }

    ItemType getItemType(Long itemType, List<ItemType> itemTypeList){
        ItemType spendAbleItemType = itemTypeList.stream()
                .filter(a -> a.getId() == itemType)
                .findAny()
                .orElse(null);
        return spendAbleItemType;
    }
}
