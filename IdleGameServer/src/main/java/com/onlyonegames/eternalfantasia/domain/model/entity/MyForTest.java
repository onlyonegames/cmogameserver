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
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    Long id;
    Long useridUser;
    String myStringValue;
    int myIntValue;
    boolean myBooleanValue;
    float myFloatValue;
}