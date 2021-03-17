package com.onlyonegames.eternalfantasia.domain.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AccessLevel;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "mainheroskillexpandinfo")
public class MainHeroSkillExpandInfo {
    @Id
    @TableGenerator(name = "hibernate_sequence")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "hibernate_sequence")
    int id;
    String code;
    int initLevel;
    int nextGradeSkillIndex;
    int openHeroLevel;
    int maxLevel;
    String skillGrade;
    String exclusiveType;
}