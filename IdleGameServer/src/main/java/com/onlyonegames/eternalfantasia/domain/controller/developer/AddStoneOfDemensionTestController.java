package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseDTO;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.UserBaseDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AddStoneOfDemensionTestController {
    private final AddStoneOfDemensionTestService addStoneOfDemensionTestService;

    @PostMapping("/api/Test/AddStoneOfDemension")
    public ResponseDTO<Map<String, Object>> Clear(@RequestBody AddStoneOfDemenstionTestDto dto) {
        Map<String, Object> map = new HashMap<>();


        Map<String, Object> response =  addStoneOfDemensionTestService.AddStoneOfDemenstion(dto.getUserId(), dto.getAddStoneOfDemenstion(), map);

        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>(HttpStatus.OK, ResponseErrorCode.NONE.getIntegerValue(), "", true, response);
        return responseDTO;
    }

}
