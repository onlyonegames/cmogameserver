package com.onlyonegames.eternalfantasia.domain.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MyForTest
{
    @Id
    //@TableGenerator(name = "hibernate_sequence", initialValue = 100, allocationSize = 1000)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long useridUser;
    String myStringValue;
    int myIntValue;
    boolean myBooleanValue;
    float myFloatValue;

    public void Set(String key, String value )
    {
        switch (key)
        {
            case "myStringValue":
                this.myStringValue = value;
                break;
            case "myIntValue":
                this.myIntValue = Integer.parseInt(value);
                break;
            case "myBooleanValue":
                this.myBooleanValue = Boolean.parseBoolean(value);
                break;
            case "myFloatValue":
                this.myFloatValue = Float.parseFloat(value);
                break;
        }
    }

    /*
    public void SetMyStringValue(String myStringValue) {
        this.myStringValue = myStringValue;
    }
     */
}