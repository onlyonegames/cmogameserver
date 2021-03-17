package com.onlyonegames.eternalfantasia.domain.controller.developer;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyCharacters;
import com.onlyonegames.eternalfantasia.domain.repository.MyCharactersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
@Transactional
public class SpendFatigabilityTestService {
    private final MyCharactersRepository myCharactersRepository;

    public Map<String, Object> Spend(Long userId, String heroCode, int chargingStartTime, Map<String, Object> map) {
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        MyCharacters myCharacters =  myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals(heroCode))
                .findAny()
                .orElse(null);
        if(myCharacters == null)
            throw new MyCustomException("Fail! -> Cause: myCharacters Can't find", ResponseErrorCode.NOT_FIND_DATA);

        myCharacters.SpendFatigability(chargingStartTime);

        map.put("myCharacters", myCharacters);
        return map;
    }

    public Map<String, Object> Add(Long userId, String heroCode, int chargingStartTime, Map<String, Object> map) {
        List<MyCharacters> myCharactersList = myCharactersRepository.findAllByuseridUser(userId);

        MyCharacters myCharacter =  myCharactersList.stream()
                .filter(a -> a.getCodeHerostable().equals(heroCode))
                .findAny()
                .orElse(null);
        if(myCharacter == null)
            throw new MyCustomException("Fail! -> Cause: myCharacters Can't find", ResponseErrorCode.NOT_FIND_DATA);

        myCharacter.FatigabilityCharging_TEST();

        map.put("myCharacter", myCharacter);
        return map;
    }
}
