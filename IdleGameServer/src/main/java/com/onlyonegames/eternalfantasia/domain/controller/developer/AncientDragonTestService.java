package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.Dungeon.MyAncientDragonExpandSaveData;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.repository.Dungeon.MyAncientDragonExpandSaveDataRepository;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@AllArgsConstructor
@Service
@Transactional
public class AncientDragonTestService {
    private final MyAncientDragonExpandSaveDataRepository myAncientDragonExpandSaveDataRepository;

    public Map<String, Object> InitPlayableRemainCount(Long userId, Map<String, Object> map) {
        MyAncientDragonExpandSaveData myAncientDragonExpandSaveData = myAncientDragonExpandSaveDataRepository.findByUseridUser(userId)
                .orElseThrow(() -> new MyCustomException("Fail! -> Cause: MyAncientDragonExpandSaveData not find.", ResponseErrorCode.NOT_FIND_DATA));


        myAncientDragonExpandSaveData.SetPlayableRemainCountForTest();

        map.put("myAncientDragonExpandSaveData", myAncientDragonExpandSaveData);
        return map;
    }
}
