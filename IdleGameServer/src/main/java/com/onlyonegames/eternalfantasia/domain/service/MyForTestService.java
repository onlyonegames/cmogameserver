package com.onlyonegames.eternalfantasia.domain.service;

import com.onlyonegames.eternalfantasia.domain.model.dto.RequestDto.ElementDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import com.onlyonegames.eternalfantasia.domain.repository.MyForTestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class MyForTestService
{
    private final MyForTestRepository myForTestRepository;

    public Map<String, Object> GetMyForTest (Long userId, Map<String, Object> map)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        map.put("myForTest", myForTest);
        return map;
    }

    public List<ElementDto> Get (Long userId, List<ElementDto> elements)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        ElementDto element = null;
        for(int i = 0; i < elements.size(); i++)
        {
            element = elements.get(i);
            switch (element.getElement())
            {
                case "myStringValue":
                    element.SetValue(myForTest.getMyStringValue());
                    break;
                case "myIntValue":
                    element.SetValue(Integer.toString(myForTest.getMyIntValue()));
                    break;
                case "myBooleanValue":
                    element.SetValue(Boolean.toString(myForTest.isMyBooleanValue()));
                    break;
                case "myFloatValue":
                    element.SetValue(Float.toString(myForTest.getMyFloatValue()));
                    break;
            }
        }

        return elements;
    }
    public List<ElementDto> Set (Long userId, List<ElementDto> elements)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        ElementDto element = null;
        for(int i = 0; i < elements.size(); i++)
        {
            element =elements.get(i);
            myForTest.Set(element.getElement(), element.getValue().toString());
        }

        return elements;
    }

    public Map<String, Object> SetMyForTest (Long userId, String key, String value, Map<String, Object> map)
    {
        MyForTest myForTest =  myForTestRepository.findByUseridUser(userId).orElse(null);
        //String json = JsonStringHerlper.WriteValueAsStringFromData(myForTest);
        //map.put("myForTest", json);
        //myForTest.SetMyStringValue(value);
        myForTest.Set(key, value);
        map.put("myForTest", myForTest);
        return map;
    }
}
