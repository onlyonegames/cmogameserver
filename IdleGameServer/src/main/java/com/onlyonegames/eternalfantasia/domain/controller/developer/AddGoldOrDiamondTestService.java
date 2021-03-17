package com.onlyonegames.eternalfantasia.domain.controller.developer;


import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ItemRequestDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.User;
import com.onlyonegames.eternalfantasia.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class AddGoldOrDiamondTestService {
    private final UserRepository userRepository;

    public Map<String, Object> AddGold(Long userId, int addCount, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));

        user.AddGold(addCount);
        map.put("user", user);
        return map;
    }

    public Map<String, Object> AddDimaond(Long userId, int addCount, Map<String, Object> map) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new MyCustomException("Fail! -> Cause: userId Can't find", ResponseErrorCode.NOT_FIND_DATA));

        user.AddDiamond(addCount);
        map.put("user", user);
        return map;
    }
}
