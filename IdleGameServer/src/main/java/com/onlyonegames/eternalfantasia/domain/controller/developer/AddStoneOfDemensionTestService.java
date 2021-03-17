package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
@Transactional
public class AddStoneOfDemensionTestService {
    private final UserRepository userRepository;
    public Map<String, Object> AddStoneOfDemenstion(Long userId, int addStoneOfDimenstion, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new MyCustomException("Fail! -> Cause: user not find userId.", ResponseErrorCode.NOT_FIND_DATA));
//        user.AddStoneOfDimension(addStoneOfDimenstion);
//        map.put("userStoneOfDimension", user.getStoneOfDimension());
        return map;
    }
}
