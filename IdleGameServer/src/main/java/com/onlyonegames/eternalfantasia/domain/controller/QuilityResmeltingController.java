package com.onlyonegames.eternalfantasia.domain.controller;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.QuilityResmeltingRequestDto;
import com.onlyonegames.eternalfantasia.domain.service.Inventory.HeroEquipmentQuilityResmeltingService;
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
@RequestMapping({"/api/QuilityResmelting"})
public class QuilityResmeltingController {
    private final HeroEquipmentQuilityResmeltingService heroEquipmentQuilityResmeltingService;

    @Autowired
    public QuilityResmeltingController(HeroEquipmentQuilityResmeltingService heroEquipmentQuilityResmeltingService) {
        this.heroEquipmentQuilityResmeltingService = heroEquipmentQuilityResmeltingService;
    }

    @PostMapping
    public ResponseDTO<Map<String, Object>> QuilityResmelting(@RequestBody QuilityResmeltingRequestDto quilityResmeltingRequestDto)
    {
        Map<String, Object> map = new HashMap<>();
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> response = heroEquipmentQuilityResmeltingService.QuilityResmelting(userId, quilityResmeltingRequestDto.getOriginalItem(), quilityResmeltingRequestDto.getEquipmentMaterial(), quilityResmeltingRequestDto.getResmeltingMaterial(), map);
        return new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
    }
}
