package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.ActiveSkillDataJsonDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyClassInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.Inventory.MyRuneInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyPixieInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyRuneLevelInfoDataDto;
import com.onlyonegames.eternalfantasia.domain.model.dto.MyStatusInfoDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.*;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyClassInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.MyRuneInventory;
import com.onlyonegames.eternalfantasia.domain.repository.*;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyClassInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyEquipmentInventoryRepository;
import com.onlyonegames.eternalfantasia.domain.repository.Inventory.MyRuneInventoryRepository;
import com.onlyonegames.eternalfantasia.etc.JsonStringHerlper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class SetterService {
    private final MyPixieInfoDataRepository myPixieInfoDataRepository;
    private final MyRuneLevelInfoDataRepository myRuneLevelInfoDataRepository;
    private final MyStatusInfoRepository myStatusInfoRepository;
    private final UserRepository userRepository;
    private final MyActiveSkillDataRepository myActiveSkillDataRepository;
    private final MyClassInventoryRepository myClassInventoryRepository;
    private final MyEquipmentInventoryRepository myEquipmentInventoryRepository;
    private final MyRuneInventoryRepository myRuneInventoryRepository;
    private final ErrorLoggingService errorLoggingService;

    public Map<String, Object> Setter(Long userId, Map<String, Map<String, String>> requestList, Map<String, Object> map) {
        Collection keys = requestList.keySet();
        Iterator itr = keys.iterator();
        while(itr.hasNext()) {
            String key = (String) itr.next();
            switch(key) {
                case "MyPixieInfoData":
//                    String json_Data = requestList.get(key);
//                    MyPixieInfoDataDto myPixieInfoDataDto = JsonStringHerlper.ReadValueFromJson(json_Data, MyPixieInfoDataDto.class);
                    break;
                case "MyRuneLevelInfoData":
                    break;
                case "MyStatusInfo":
                    break;
                case "User":
                    break;
                case "myActiveSkillData":
                    break;
                case "myClassInventory":
                    break;
                case "myEquipmentInventory":
                    break;
                case "myRuneInventory":
                    break;
            }
        }
        return map;
    }
}
