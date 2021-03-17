package com.onlyonegames.eternalfantasia.domain.controller;
import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.StrengthenRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.HeroEquipmentStrengthenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/api/StrengthenEquipment"})
public class StrengthenEquipmentController {
    private final HeroEquipmentStrengthenService heroEquipmentStrengthenService;
    @Autowired
    public StrengthenEquipmentController(HeroEquipmentStrengthenService heroEquipmentStrengthenService) {
        this.heroEquipmentStrengthenService = heroEquipmentStrengthenService;
    }
    @PostMapping
    public ResponseDTO<Map<String, Object>> StrengthenEquipment(@RequestBody StrengthenRequestDto strengthenRequestDto) {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentStrengthenService.StrengthenEquipment(userId, strengthenRequestDto.getOriginalItem(), strengthenRequestDto.getMaterialItemList(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
