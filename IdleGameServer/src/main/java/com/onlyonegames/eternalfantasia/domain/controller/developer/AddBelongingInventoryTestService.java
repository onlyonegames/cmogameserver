package com.onlyonegames.eternalfantasia.domain.controller.developer;


import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.BelongingCharacterPieceTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.SpendableItemInfoTable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class AddBelongingInventoryTestService {
    private final BelongingInventoryRepository belongingInventoryRepository;
    private final GameDataTableService gameDataTableService;
    private final ItemTypeRepository itemTypeRepository;
    public Map<String, Object> AddBelongingInventory(Long userId, List<ItemRequestDto> listItems, Map<String, Object> map) {

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        List<SpendableItemInfoTable> spendableItemInfoTableList = gameDataTableService.SpendableItemInfoTableList();
        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        for(ItemRequestDto itemRequestDto : listItems) {
            BelongingInventory belongingInventory = belongingInventoryList.stream()
                    .filter(a -> a.getId().equals(itemRequestDto.getId()))
                    .findAny()
                    .orElse(null);
            if(belongingInventory != null) {
                belongingInventory.AddItem(itemRequestDto.getCount(), 99999);
            }
            else {
                String itemCode = itemRequestDto.getCode();

                SpendableItemInfoTable spendableItemInfoTable = spendableItemInfoTableList.stream()
                        .filter(a -> a.getCode().equals(itemCode))
                        .findAny()
                        .orElse(null);
                if(spendableItemInfoTable != null) {
                    ItemType itemType = null;
                    if(itemCode.contains("enchant") || itemCode.contains("resmelt")) {

                        itemType = itemTypeList.stream()
                                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Spendables)
                                .findAny()
                                .orElse(null);
                    }
                    else {
                        itemType = itemTypeList.stream()
                                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_Material)
                                .findAny()
                                .orElse(null);
                    }
                    BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                    belongingInventoryDto.setItemId(spendableItemInfoTable.getId());
                    belongingInventoryDto.setCount(itemRequestDto.getCount());
                    belongingInventoryDto.setItemType(itemType);
                    belongingInventoryDto.setUseridUser(userId);

                    BelongingInventory generateBelongingInventory = belongingInventoryDto.ToEntity();
                    belongingInventoryRepository.save(generateBelongingInventory);
                }
                else{
                    String characterPieceCode = itemRequestDto.getCode();

                    BelongingCharacterPieceTable belongingCharacterPieceTable = belongingCharacterPieceTableList.stream()
                            .filter(a -> a.getCode().equals(characterPieceCode))
                            .findAny()
                            .orElse(null);
                    if(belongingCharacterPieceTable != null) {
                        ItemType itemType = null;
                        itemType = itemTypeList.stream()
                                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                                .findAny()
                                .orElse(null);
                        BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
                        belongingInventoryDto.setItemId(belongingCharacterPieceTable.getId());
                        belongingInventoryDto.setCount(itemRequestDto.getCount());
                        belongingInventoryDto.setItemType(itemType);
                        belongingInventoryDto.setUseridUser(userId);

                        BelongingInventory generateBelongingInventory = belongingInventoryDto.ToEntity();
                        belongingInventoryRepository.save(generateBelongingInventory);
                    }

                }
            }
        }

        map.put("MyBelongingInventory", belongingInventoryList);
        return map;
    }

}
