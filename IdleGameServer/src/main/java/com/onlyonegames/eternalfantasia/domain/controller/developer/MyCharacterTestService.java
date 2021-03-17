package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.ItemTypeName;
import com.onlyonegames.eternalfantasia.domain.model.dto.BelongingInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.BelongingInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.ItemType;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.BelongingCharacterPieceTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.herostable;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.BelongingInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.ItemTypeRepository;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import com.onlyonegames.eternalfantasia.domain.service.GameDataTableService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
@Transactional
public class MyCharacterTestService {
    private final MyCharactersRepository myCharactersRepository;
    private final GameDataTableService gameDataTableService;
    private final ItemTypeRepository itemTypeRepository;
    private final BelongingInventoryRepository belongingInventoryRepository;
    public Map<String, Object> Clear(Long userId, Map<String, Object> map) {
        List<herostable> herostableList = gameDataTableService.HerosTableList();
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);
        for(MyCharacters myCharacters : myCharactersList) {
            if(myCharacters.getCodeHerostable().equals("hero"))
                continue;
            herostable herostable = herostableList.stream().filter(a -> a.getCode().equals(myCharacters.getCodeHerostable())).findAny().orElse(null);
            myCharacters.ClearMyCharacters(herostable.getTier());
        }
        map.put("myCharactersList", myCharactersList);
        return map;
    }

    public Map<String, Object> AddPiece(Long userId, String heroCode, int count,  Map<String, Object> map) {

        int gettingCount = 20;
        String characterPieceCode = heroCode;
        List<ItemType> itemTypeList = itemTypeRepository.findAll();
        ItemType characterPieceItemType = itemTypeList.stream()
                .filter(a -> a.getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece)
                .findAny()
                .orElse(null);

        List<BelongingCharacterPieceTable> belongingCharacterPieceTableList = gameDataTableService.BelongingCharacterPieceTableList();
        BelongingCharacterPieceTable cr_000_characterPiece = belongingCharacterPieceTableList.stream()
                .filter(a -> a.getCode().contains(characterPieceCode))
                .findAny()
                .orElse(null);
        if(cr_000_characterPiece == null)
            throw new MyCustomException("Fail! -> Cause: Can't Find characterPiece.", ResponseErrorCode.NOT_FIND_DATA);

        List<BelongingInventory> belongingInventoryList = belongingInventoryRepository.findByUseridUser(userId);
        BelongingInventory myCharacterPieceItem = belongingInventoryList.stream()
                .filter(a -> a.getItemType().getItemTypeName() == ItemTypeName.BelongingItem_CharacterPiece && a.getItemId() == cr_000_characterPiece.getId())
                .findAny()
                .orElse(null);

        if(myCharacterPieceItem == null) {
            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(cr_000_characterPiece.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(characterPieceItemType);
            myCharacterPieceItem = belongingInventoryDto.ToEntity();
            myCharacterPieceItem = belongingInventoryRepository.save(myCharacterPieceItem);
            belongingInventoryList.add(myCharacterPieceItem);
        }
        else {
            myCharacterPieceItem.AddItem(gettingCount, cr_000_characterPiece.getStackLimit());

            BelongingInventoryDto belongingInventoryDto = new BelongingInventoryDto();
            belongingInventoryDto.setUseridUser(userId);
            belongingInventoryDto.setItemId(cr_000_characterPiece.getId());
            belongingInventoryDto.setCount(gettingCount);
            belongingInventoryDto.setItemType(characterPieceItemType);
        }

        map.put("myCharacterPieceItem", myCharacterPieceItem);
        return map;
    }
}
