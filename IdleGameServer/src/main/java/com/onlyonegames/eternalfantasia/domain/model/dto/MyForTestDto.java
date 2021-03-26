package com.onlyonegames.eternalfantasia.domain.model.dto;

import com.onlyonegames.eternalfantasia.domain.model.entity.MyForTest;
import lombok.Data;

@Data
public class MyForTestDto
{
    Long useridUser;
    String myStringValue;
    int myIntValue;
    boolean myBooleanValue;
    float myFloatValue;

    public MyForTest ToEntity()
    {
        return MyForTest.builder().useridUser(useridUser).myBooleanValue(myBooleanValue).myFloatValue(myFloatValue).myIntValue(myIntValue).myStringValue(myStringValue).build();
    }
}
