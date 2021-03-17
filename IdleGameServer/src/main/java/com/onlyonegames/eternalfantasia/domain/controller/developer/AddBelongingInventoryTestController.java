package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AddBelongingInventoryTestController {
    private final AddBelongingInventoryTestService addBelongingInventoryTestService;

    @PostMapping("/api/Test/AddBelongingInventory")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody AddBelongingInventoryTestDto dto) {
        Map<String, Object> map = new HashMap<>();


        Map<String, Object> response =  addBelongingInventoryTestService.AddBelongingInventory(dto.getUserId(), dto.getListItems(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }
}
