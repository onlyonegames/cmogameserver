package com.onlyonegames.eternalfantasia.etc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;

import java.io.IOException;

public class JsonStringHerlper {

    //Json 형태의 String을 T 형의 Data로 리턴
    public static <T> T ReadValueFromJson(String jsonString, Class<T> valueType) {
        T data = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            data = mapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyCustomException("Fail -> Cause: Invaild JsonString to Data => " + jsonString, ResponseErrorCode.INVAILD_JSON_STRING_TO_DATA);
        }
        return data;
    }
    //특정 Object를 Json 형태의 String로 리턴
    public static String WriteValueAsStringFromData(Object value) {
        String json = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new MyCustomException("Fail -> Cause: Invaild data to JsonString ", ResponseErrorCode.INVAILD_DATA_TO_JSON_STRING);
        }
        return json;
    }
}
