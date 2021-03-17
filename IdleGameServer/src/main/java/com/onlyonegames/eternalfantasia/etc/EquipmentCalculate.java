package com.onlyonegames.eternalfantasia.etc;

import com.onlyonegames.eternalfantasia.domain.MyCustomException;
import com.onlyonegames.eternalfantasia.domain.ResponseErrorCode;
import com.onlyonegames.eternalfantasia.domain.model.dto.HeroEquipmentInventoryDto;
import com.onlyonegames.eternalfantasia.domain.model.entity.Inventory.HeroEquipmentInventory;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.EquipmentOptionsInfoTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentClassProbabilityTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentProductionExpandTable;
import com.onlyonegames.eternalfantasia.domain.model.gamedatas.HeroEquipmentsTable;
import com.onlyonegames.util.MathHelper;
import com.onlyonegames.util.StringMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static com.onlyonegames.util.MathHelper.RandomIndexWidthProbability;
import static com.onlyonegames.util.MathHelper.Range;

public class EquipmentCalculate {
    // Array 순서대로 각Class : SSS, SS, S, A, B, C, D
    public static String[] classCategoryArray = {"SSS","SS","S","A","B","C","D"};
//    private static int[] classValuesArray = { 100, 80, 60, 50, 30, 20, 10 };
//    private static double[] normalClassProbabilityArray = { 2.0, 3.0, 7.0, 10.0, 30.0, 30.0, 18.0 };
//    private static double[] rareClassProbabilityArray = { 1.50, 2.50, 5.0, 10.0, 20.0, 33.0, 28.0 };
//    private static double[] heroClassProbabilityArray = { 1.0, 2.50, 3.50, 8.0, 20.0, 35.0, 30.0 };
//    private static double[] legendClassProbabilityArray = { 0.70, 2.0, 3.30, 8.0, 18.0, 33.0, 35.0 };
//    private static double[] divineClassProbabilityArray = { 0.50, 2.0, 2.50, 6.0, 16.0, 33.0, 40.0 };
//    private static double[] ancientClassProbabilityArray = { 0.20, 1.50, 2.0, 5.0, 11.30, 30.0, 50.0 };
    //등급별 고유 값
    public static int GradeValue(String itemGradeString) {
        int returnValue;
        switch (itemGradeString) {
            case "Normal":
                returnValue = 1;
                break;
            case "Rare":
                returnValue = 2;
                break;
            case "Hero":
                returnValue = 3;
                break;
            case "Legend":
                returnValue = 4;
                break;
            case "Divine":
                returnValue = 5;
                break;
            case "Ancient":
                returnValue = 6;
                break;
            default:
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }
    //등급별 최고 강화 가능 레벨
    public static int MaxLevel(String itemGradeString) {
        int returnValue = 0;
        switch (itemGradeString) {
            case "Normal":
                returnValue = 10;
                break;
            case "Rare":
                returnValue = 20;
                break;
            case "Hero":
                returnValue = 30;
                break;
            case "Legend":
                returnValue = 40;
                break;
            case "Divine":
                returnValue = 50;
                break;
            case "Ancient":
                returnValue = 60;
                break;
            default:
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }
    //재료 투입량에 따른 제작 장비 등급 확률
    static double CalculateGradeFromMaterialCount(HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable, int totalUsingMaterialsCount) {
        String grade = heroEquipmentProductionExpandTable.getGrade();
        double baseGradeProbability = heroEquipmentProductionExpandTable.getProductionPercent();
        double resultProbability = 0;
        switch (grade) {
            case "Rare":
                resultProbability = baseGradeProbability - ((baseGradeProbability * (totalUsingMaterialsCount - 150) * 0.002) / 2);
                break;
            case "Hero":
                resultProbability = baseGradeProbability + ((baseGradeProbability * (totalUsingMaterialsCount - 150) * 0.001));
                break;
            case "Legend":
                resultProbability = baseGradeProbability + (baseGradeProbability * (totalUsingMaterialsCount - 150) * 0.005);
                break;
            case "Divine":
            case "Ancient":
                resultProbability = baseGradeProbability + (baseGradeProbability * (totalUsingMaterialsCount - 150) * 0.003);
                break;
        }
        return resultProbability;
    }
    //제작될 장비의 등급 결정
    public static String DecideGrade(List<HeroEquipmentProductionExpandTable> heroEquipmentProductionExpandTableList, int totalUsingMaterialsCount) {
        List<Double> gradeProbabilityList = new ArrayList<>();
        for(HeroEquipmentProductionExpandTable heroEquipmentProductionExpandTable : heroEquipmentProductionExpandTableList) {
            Double baseGradeProbability = CalculateGradeFromMaterialCount(heroEquipmentProductionExpandTable, totalUsingMaterialsCount);

            gradeProbabilityList.add(baseGradeProbability);
        }
        int selectedIndex = MathHelper.RandomIndexWidthProbability(gradeProbabilityList);
        return heroEquipmentProductionExpandTableList.get(selectedIndex).getGrade();
    }
    //장비 기본 능력치 결정 계산식
    public static double CalculateEquipmentValue(double baseValue, int level, int gradeValue, int classValue, double growValue, String valueKind/*퍼센트 값(소수) 인지, 정수 값인지.*/) {

        double sum = ((baseValue + (baseValue * ((classValue - 1) * (classValue * 0.5)))) + (baseValue * ((level - 1) * growValue))) + (baseValue * (gradeValue - 1) * (gradeValue * 0.5));
        double value = MathHelper.Round3(sum);
        if(valueKind.equals("FIXED_VALUE")) {
            value = Math.round(value);
        }
        return value;
    }

    //등급별 생성 옵션 갯수
    public static int GetOptionCount(String itemGradeString) {
        int returnValue;
        switch (itemGradeString) {
            case "Normal":
                returnValue = 0;
                break;
            case "Rare":
                returnValue = 1;
                break;
            case "Hero":
                returnValue = 2;
                break;
            case "Legend":
                returnValue = 3;
                break;
            case "Divine":
            case "Ancient":
                returnValue = 4;
                break;
            default:
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        return returnValue;
    }
    //장비 옵션 기본 능력치 결정 계산식
    public static double CalculateOptionValue(double baseValue, int level, int gradeValue, int classValue, String valueKind/*퍼센트 값(소수) 인지, 정수 값인지.*/) {
        double sum = (baseValue + (baseValue * (gradeValue - 1) * ((classValue - 1) * 0.5))) + (baseValue * ((classValue - 1) * (gradeValue * 0.5)));

        double value = MathHelper.Round3(sum);
        if(valueKind.equals("FIXED_VALUE")) {
            value = Math.round(value);
        }
        return value;
    }
    //classCategory로 classValu 얻기
    public static double GetClassValueFromClassCategory(String classString, HeroEquipmentClassProbabilityTable classValues) {
        double returnValue = 0;
        switch (classString) {
            case "SSS":
                returnValue = classValues.getSSS();
                break;
            case "SS":
                returnValue = classValues.getSS();
                break;
            case "S":
                returnValue = classValues.getS();
                break;
            case "A":
                returnValue = classValues.getA();
                break;
            case "B":
                returnValue = classValues.getB();
                break;
            case "C":
                returnValue = classValues.getC();
                break;
            case "D":
                returnValue = classValues.getD();
                break;
        }
        return returnValue;
    }
    //품질 제련 이후 classCategory 얻기
    public static String GetClassCategoryAfterQuilityResmelting(String prevClassString) {
        if(prevClassString.equals("SSS"))
            return null;

        String returnValue = null;
        switch (prevClassString) {
            case "SS":
                returnValue = "SSS";
                break;
            case "S":
                returnValue = "SS";
                break;
            case "A":
                returnValue = "S";
                break;
            case "B":
                returnValue = "A";
                break;
            case "C":
                returnValue = "B";
                break;
            case "D":
                returnValue = "C";
                break;
        }
        return returnValue;
    }
    //장비 테이블로부터 제작할 아이템의 종류와 특정 등급을 변수로 받아 후보 장비 리스트 리턴
    public static List<HeroEquipmentsTable> GetProbabilityList(List<HeroEquipmentsTable> heroEquipmentsTableList, EquipmentItemCategory equipmentItemCategory, String itemGradeString) {
        List<HeroEquipmentsTable> probabilityList = GetEquipmentsTableListByCategory(heroEquipmentsTableList, equipmentItemCategory);
        if(itemGradeString != null){
            probabilityList = probabilityList.stream()
                    .filter(a -> a.getGrade().equals(itemGradeString))
                    .collect(Collectors.toList());
        }
        return probabilityList;
    }
    //카테고리별 장비 아이템 리스트 리턴
    public static List<HeroEquipmentsTable> GetEquipmentsTableListByCategory(List<HeroEquipmentsTable> heroEquipmentsTableList, EquipmentItemCategory equipmentItemCategory) {
        List<HeroEquipmentsTable> probabilityList = null;
        switch (equipmentItemCategory) {
            case WEAPON:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Sword")
                                || a.getKind().equals("Spear")
                                || a.getKind().equals("Bow")
                                || a.getKind().equals("Gun")
                                || a.getKind().equals("Wand"))
                        .collect(Collectors.toList());
                break;
            case ARMOR:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Armor"))
                        .collect(Collectors.toList());
                break;
            case HELMET:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Helmet"))
                        .collect(Collectors.toList());
                break;
            case ACCESSORY:
                probabilityList = heroEquipmentsTableList.stream()
                        .filter(a -> a.getKind().equals("Accessory"))
                        .collect(Collectors.toList());
                break;
            case ALL:
                probabilityList = heroEquipmentsTableList;
                break;
        }
        List<HeroEquipmentsTable> deleteList = new ArrayList<>();
        boolean deleted = false;
        //by rainful 2021.03.09 균열의 보스 아이템들만 빼주고 리턴.
        for(HeroEquipmentsTable heroEquipmentsTable : probabilityList) {
            String code = heroEquipmentsTable.getCode();
            if(code.contains("99") || code.contains("97")){
                deleteList.add(heroEquipmentsTable);
                deleted = true;
            }
        }

        List<HeroEquipmentsTable> returnProbabilityList = new ArrayList<>();
        returnProbabilityList.addAll(probabilityList);
        if(deleted) {
            returnProbabilityList.removeAll(deleteList);
        }
        return returnProbabilityList;
    }

    //최초 무료 장비 생성
    public static HeroEquipmentInventory CreateFirstWeapon(Long userId, HeroEquipmentsTable heroEquipment, HeroEquipmentClassProbabilityTable classValues, EquipmentOptionsInfoTable equipmentOptionsInfoTable) {
        return CreateFreeEquipmentItem(userId, heroEquipment, "C", classValues, equipmentOptionsInfoTable);
    }

    //강제 class 고정 무기 생성.최초 무료 장비 및 스테이지 클리어 보상에서 획득할 아이템들에 활용중.
    public static HeroEquipmentInventory CreateFreeEquipmentItem(Long userId, HeroEquipmentsTable heroEquipment, String fixedItemClass, HeroEquipmentClassProbabilityTable classValues, EquipmentOptionsInfoTable equipmentOptionsInfoTable) {
        //결정된 값으로 장비 생성
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setLevel(1);
        dto.setItemClass(fixedItemClass);
        dto.setUseridUser(userId);
        dto.setItem_Id(heroEquipment.getId());


        dto.setExp(0);
        String itemGradeString = heroEquipment.getGrade();
        dto.setMaxLevel(MaxLevel(itemGradeString));
        //등급별 고유값
        int gradeValue = GradeValue(itemGradeString);
        //레벨업에 필요한 경험치
        int nextExp = CalculateNeedExp(dto.getLevel(), gradeValue);
        int classValue = (int)GetClassValueFromClassCategory(fixedItemClass, classValues);
        dto.setNextExp(nextExp);
        dto.setItemClassValue(classValue);
        if(equipmentOptionsInfoTable == null){
            dto.setOptionIds("");
            dto.setOptionValues("");
        }
        else{
            int optionId = equipmentOptionsInfoTable.getID();
            dto.setOptionIds(String.valueOf(optionId));
            Double optionValues = CalculateOptionValue(equipmentOptionsInfoTable.getBaseValue(), 1, gradeValue, classValue, equipmentOptionsInfoTable.getPERCENT_OR_FIXEDVALUE());
            dto.setOptionValues(optionValues.toString());
        }

        //장비 최종 기본값 결정
        float decideDefaultValue = (float)CalculateEquipmentValue(heroEquipment.getDefaultAbilityValue(), 1, gradeValue, classValue, heroEquipment.getDefaultGrow(), heroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        float decideSecondValue = (float)CalculateEquipmentValue(heroEquipment.getSecondAbilityValue(), 1, gradeValue, classValue, heroEquipment.getSecondGrow(), heroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        dto.setDecideSecondAbilityValue(decideSecondValue);
        return dto.ToEntity();
    }
    //신규 장비 생성(정해진 품질의 장비 생성)
    public static HeroEquipmentInventory CreateEquipment(Long userId, HeroEquipmentsTable heroEquipment, String itemClass, int classValue, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList) {

        int level = 1;
        String itemGradeString = heroEquipment.getGrade();
        //등급별 고유값
        int gradeValue = GradeValue(itemGradeString);
        //장비 최종 기본값 결정
        float decideDefaultValue = (float)CalculateEquipmentValue(heroEquipment.getDefaultAbilityValue(), level, gradeValue, classValue, heroEquipment.getDefaultGrow(), heroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        float decideSecondValue = (float)CalculateEquipmentValue(heroEquipment.getSecondAbilityValue(), level, gradeValue, classValue, heroEquipment.getSecondGrow(), heroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        //레벨업에 필요한 경험치
        int nextExp = CalculateNeedExp(level, gradeValue);
        //결정된 값으로 장비 생성
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(userId);
        dto.setItem_Id(heroEquipment.getId());
        dto.setItemClassValue(classValue);
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        dto.setDecideSecondAbilityValue(decideSecondValue);
        dto.setLevel(1);
        dto.setMaxLevel(MaxLevel(itemGradeString));
        dto.setExp(0);
        dto.setNextExp(nextExp);
        dto.setItemClass(itemClass);
        //장비 등급에 따라 옵션 결정
        SelectOptions(equipmentOptionsInfoTableList, itemGradeString, level, gradeValue, classValue, dto);
        return dto.ToEntity();
    }

    //신규 장비 생성(품질 등급 랜덤 생성)
    public static HeroEquipmentInventory CreateEquipment(Long userId, HeroEquipmentsTable heroEquipment, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList, List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTableList) {

        int level = 1;
        String itemGradeString = heroEquipment.getGrade();
        //등급별 고유값
        int gradeValue = GradeValue(itemGradeString);
        //클래스(퀄리티등급:SSS~D) 결정
        int classIndex = 0;
        List<Double> classProbabilityList = new ArrayList<>();
        HeroEquipmentClassProbabilityTable heroEquipmentClassProbabilityTable = null;
        switch (itemGradeString) {
            case "Normal":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityNormal"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            case "Rare":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityRare"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            case "Hero":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityHero"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            case "Legend":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityLegend"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            case "Divine":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityDivine"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            case "Ancient":
                heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                        .filter(a -> a.getCategory().equals("fixedClassProbabilityAncient"))
                        .findAny()
                        .orElse(null);
                classIndex = getClassIndex(classProbabilityList, heroEquipmentClassProbabilityTable);
                break;
            default:
                throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        }
        heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                .filter(a -> a.getCategory().equals("classValue"))
                .findAny()
                .orElse(null);
        if(heroEquipmentClassProbabilityTable == null)
            throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);

        String itemClass = classCategoryArray[classIndex];
        int classValue = (int)GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTable);
        //장비 최종 기본값 결정
        float decideDefaultValue = (float)CalculateEquipmentValue(heroEquipment.getDefaultAbilityValue(), level, gradeValue, classValue, heroEquipment.getDefaultGrow(), heroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        float decideSecondValue = (float)CalculateEquipmentValue(heroEquipment.getSecondAbilityValue(), level, gradeValue, classValue, heroEquipment.getSecondGrow(), heroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        //레벨업에 필요한 경험치
        int nextExp = CalculateNeedExp(level, gradeValue);
        //결정된 값으로 장비 생성
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(userId);
        dto.setItem_Id(heroEquipment.getId());
        dto.setItemClassValue(classValue);
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        dto.setDecideSecondAbilityValue(decideSecondValue);
        dto.setLevel(1);
        dto.setMaxLevel(MaxLevel(itemGradeString));
        dto.setExp(0);
        dto.setNextExp(nextExp);
        dto.setItemClass(itemClass);
        //장비 등급에 따라 옵션 결정
        SelectOptions(equipmentOptionsInfoTableList, itemGradeString, level, gradeValue, classValue, dto);
        return dto.ToEntity();
    }

    private static int getClassIndex(List<Double> classProbabilityList, HeroEquipmentClassProbabilityTable heroEquipmentClassProbabilityTable) {
        int classIndex = 0;
        if(heroEquipmentClassProbabilityTable != null) {

            classProbabilityList.add(heroEquipmentClassProbabilityTable.getSSS());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getSS());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getS());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getA());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getB());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getC());
            classProbabilityList.add(heroEquipmentClassProbabilityTable.getD());
            classIndex = RandomIndexWidthProbability(classProbabilityList);
        }
        return classIndex;
    }

    //장비 제작시 아이템 생성
    public static HeroEquipmentInventory ProductEquipment(Long userId, HeroEquipmentsTable heroEquipment, int totalUsingMaterialsCount, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList, List<HeroEquipmentClassProbabilityTable> heroEquipmentClassProbabilityTableList) {

        int level = 1;
        String itemGradeString = heroEquipment.getGrade();
        //등급별 고유값
        int gradeValue = GradeValue(itemGradeString);
        //클래스(퀄리티등급:SSS~D) 결정
        int classIndex = 0;
        List<Double> classProbabilityList = new ArrayList<>();
        HeroEquipmentClassProbabilityTable heroEquipmentClassProbabilityTable = null;

        heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                .filter(a -> a.getCategory().equals("baseClassProbability"))
                .findAny()
                .orElse(null);
        if(heroEquipmentClassProbabilityTable == null)
            throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);
        //재료 투입량에 따른 제작 장비 품질 확률
        if(heroEquipmentClassProbabilityTable != null) {

            double getSSSProbability = heroEquipmentClassProbabilityTable.getSSS();
            double calculateSSSProbability = getSSSProbability + (getSSSProbability * (totalUsingMaterialsCount-150)*0.001);
            classProbabilityList.add(calculateSSSProbability);

            double getSSProbability = heroEquipmentClassProbabilityTable.getSS();
            double calculateSSProbability = getSSProbability + (getSSProbability * (totalUsingMaterialsCount-150)*0.001);
            classProbabilityList.add(calculateSSProbability);

            double getSProbability = heroEquipmentClassProbabilityTable.getS();
            double calculateSProbability = getSProbability + (getSProbability * (totalUsingMaterialsCount-150)*0.001);
            classProbabilityList.add(calculateSProbability);

            double getAProbability = heroEquipmentClassProbabilityTable.getA();
            double calculateAProbability = getAProbability + (getAProbability * (totalUsingMaterialsCount-150)*0.002);
            classProbabilityList.add(calculateAProbability);

            double getBProbability = heroEquipmentClassProbabilityTable.getB();
            double calculateBProbability = getBProbability + (getBProbability * (totalUsingMaterialsCount-150)*0.003);
            classProbabilityList.add(calculateBProbability);

            double getCProbability = heroEquipmentClassProbabilityTable.getC();
            double calculateCProbability = getCProbability - ((getCProbability * (totalUsingMaterialsCount-150)*0.001)/5);
            classProbabilityList.add(calculateCProbability);

            double getDProbability = heroEquipmentClassProbabilityTable.getD();
            double calculateDProbability = getDProbability - ((getDProbability * (totalUsingMaterialsCount-150)*0.001)/1.5);
            classProbabilityList.add(calculateDProbability);
            classIndex = RandomIndexWidthProbability(classProbabilityList);
        }

        heroEquipmentClassProbabilityTable = heroEquipmentClassProbabilityTableList.stream()
                .filter(a -> a.getCategory().equals("classValue"))
                .findAny()
                .orElse(null);
        if(heroEquipmentClassProbabilityTable == null)
            throw new MyCustomException("Fail! -> Cause: Undefined itemGrade.", ResponseErrorCode.UNDEFINED);

        String itemClass = classCategoryArray[classIndex];
        int classValue = (int)GetClassValueFromClassCategory(itemClass, heroEquipmentClassProbabilityTable);
        //장비 최종 기본값 결정
        float decideDefaultValue = (float)CalculateEquipmentValue(heroEquipment.getDefaultAbilityValue(), level, gradeValue, classValue, heroEquipment.getDefaultGrow(), heroEquipment.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        float decideSecondValue = (float)CalculateEquipmentValue(heroEquipment.getSecondAbilityValue(), level, gradeValue, classValue, heroEquipment.getSecondGrow(), heroEquipment.getSECOND_PERCENT_OR_FIXEDVALUE());
        //레벨업에 필요한 경험치
        int nextExp = CalculateNeedExp(level, gradeValue);
        //결정된 값으로 장비 생성
        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setUseridUser(userId);
        dto.setItem_Id(heroEquipment.getId());
        dto.setItemClassValue(classValue);
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        dto.setDecideSecondAbilityValue(decideSecondValue);
        dto.setLevel(1);
        dto.setMaxLevel(MaxLevel(itemGradeString));
        dto.setExp(0);
        dto.setNextExp(nextExp);
        dto.setItemClass(itemClass);
        //장비 등급에 따라 옵션 결정
        SelectOptions(equipmentOptionsInfoTableList, itemGradeString, level, gradeValue, classValue, dto);
        return dto.ToEntity();
    }
    //이전 장비 승급
    public static HeroEquipmentInventoryDto PromotionEquipment(HeroEquipmentInventory originalItem, HeroEquipmentsTable willPromotionItemInfo, List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList, boolean isRepromotion) {

        int level = 1;
        String itemGradeString = willPromotionItemInfo.getGrade();
        //등급 결정
        int gradeValue = GradeValue(itemGradeString);
        //승급시 클래스 승계
        int classValue = originalItem.getItemClassValue();
        //장비 최종 기본값 결정
        float decideDefaultValue = (float)CalculateEquipmentValue(willPromotionItemInfo.getDefaultAbilityValue(), level, gradeValue, classValue, willPromotionItemInfo.getDefaultGrow(), willPromotionItemInfo.getDEFAULT_PERCENT_OR_FIXEDVALUE());
        float decideSecondValue = (float)CalculateEquipmentValue(willPromotionItemInfo.getSecondAbilityValue(), level, gradeValue, classValue, willPromotionItemInfo.getSecondGrow(), willPromotionItemInfo.getSECOND_PERCENT_OR_FIXEDVALUE());
        //레벨업에 필요한 경험치
        int nextExp = CalculateNeedExp(level, gradeValue);
        //결정된 값으로 장비 생성

        HeroEquipmentInventoryDto dto = new HeroEquipmentInventoryDto();
        dto.setLevel(1);
        dto.setUseridUser(originalItem.getUseridUser());
        dto.setDecideDefaultAbilityValue(decideDefaultValue);
        dto.setDecideSecondAbilityValue(decideSecondValue);
        dto.setItem_Id(willPromotionItemInfo.getId());
        dto.setItemClass(originalItem.getItemClass());
        dto.setItemClassValue(classValue);
        dto.setExp(0);
        dto.setNextExp(nextExp);
        dto.setMaxLevel(MaxLevel(itemGradeString));

        dto.setOptionIds(originalItem.getOptionIds());
        dto.setOptionValues("");//옵션값 재적용.
        //dto.setOptionValues(originalItem.getOptionValues());
        if(isRepromotion) {
            //다이아를 사용한 재승급의 경우 마지막 옵션은 삭제
            int lastCommaIndex = dto.getOptionIds().lastIndexOf(",");
            dto.setOptionIds(dto.getOptionIds().substring(0, lastCommaIndex));

            //lastCommaIndex = dto.getOptionValues().lastIndexOf(",");
            //dto.setOptionValues(dto.getOptionValues().substring(0, lastCommaIndex));
        }

        //승급시 이전 옵션은 승계하고 옵션 1개 추가
        List<EquipmentOptionsInfoTable> normalOptions = equipmentOptionsInfoTableList.stream()
                .filter(a -> !a.isOnlySmeltingOption())
                .collect(Collectors.toList());
        String[] previousOptions = dto.getOptionIds().split(",");
        if(previousOptions[0].length() == 0) {//옵션이 없는 경우. 튜토리얼 무기는 옵션 없음.
            int remainNormalOptionsCount = normalOptions.size();
            int selectedIndex = (int)MathHelper.Range(0, remainNormalOptionsCount);
            EquipmentOptionsInfoTable newOption = normalOptions.get(selectedIndex);
            double decideOptionValue = CalculateOptionValue(newOption.getBaseValue(), level, gradeValue, classValue, newOption.getPERCENT_OR_FIXEDVALUE());
            StringMaker.Clear();
            StringMaker.stringBuilder.append(newOption.getID());
            dto.setOptionIds(StringMaker.stringBuilder.toString());
            StringMaker.Clear();
            StringMaker.stringBuilder.append(decideOptionValue);
            dto.setOptionValues(StringMaker.stringBuilder.toString());
        }
        else {
            //이전 옵션 값 재계산.
            String optionsValues = "";
            StringMaker.Clear();
            StringMaker.stringBuilder.append(optionsValues);
            int haveOptionsCount = dto.getOptionIds().split(",").length;

            for(int i = 0; i < haveOptionsCount; i++){
                int previousOptionId = Integer.parseInt(previousOptions[i]);
                EquipmentOptionsInfoTable optionsInfoTable = equipmentOptionsInfoTableList.stream().filter(a -> a.getID() == previousOptionId).findAny().orElse(null);

                if (optionsValues.length() > 0)
                    StringMaker.stringBuilder.append(",");
                double decideOptionValue = CalculateOptionValue(optionsInfoTable.getBaseValue(), level, gradeValue, classValue, optionsInfoTable.getPERCENT_OR_FIXEDVALUE());
                StringMaker.stringBuilder.append(decideOptionValue);
                optionsValues = StringMaker.stringBuilder.toString();
            }
            dto.setOptionValues(optionsValues);
            //고대 등급의 옵션 갯수는 신성과 동일해서 옵션이 추가 되지 않는다.
            int optionCount = GetOptionCount(itemGradeString);
            if(optionCount > haveOptionsCount) {
                //새롭게 추가될 옵션 후보군 셋팅
                for(int i = 0; i < haveOptionsCount; i++) {
                    int previousOption = Integer.parseInt(previousOptions[i]);
                    for(int j = 0; j < normalOptions.size(); j++) {
                        if(normalOptions.get(j).getID() == previousOption) {
                            normalOptions.remove(j);
                            break;
                        }
                    }
                }
                int remainNormalOptionsCount = normalOptions.size();
                int selectedIndex = (int)MathHelper.Range(0, remainNormalOptionsCount);
                EquipmentOptionsInfoTable newOption = normalOptions.get(selectedIndex);
                double decideOptionValue = CalculateOptionValue(newOption.getBaseValue(), level, gradeValue, classValue, newOption.getPERCENT_OR_FIXEDVALUE());
                StringMaker.Clear();
                StringMaker.stringBuilder.append(dto.getOptionIds());
                StringMaker.stringBuilder.append(",");
                StringMaker.stringBuilder.append(newOption.getID());
                dto.setOptionIds(StringMaker.stringBuilder.toString());
                StringMaker.Clear();
                StringMaker.stringBuilder.append(dto.getOptionValues());
                StringMaker.stringBuilder.append(",");
                StringMaker.stringBuilder.append(decideOptionValue);
                dto.setOptionValues(StringMaker.stringBuilder.toString());
            }
        }
        return dto;
    }
    //옵션 설정
    private static void SelectOptions(List<EquipmentOptionsInfoTable> equipmentOptionsInfoTableList, String itemGradeString, int level, int gradeValue, int classValue, HeroEquipmentInventoryDto heroEquipment) {

        String optionsIds = "";
        String optionsValues = "";
        List<EquipmentOptionsInfoTable> normalOptions = equipmentOptionsInfoTableList.stream()
                .filter(a -> !a.isOnlySmeltingOption())
                .collect(Collectors.toList());
        int optionCount = GetOptionCount(itemGradeString);
        List<EquipmentOptionsInfoTable> selectedOptions = new ArrayList<>();
        for (int i = 0; i < optionCount; i++) {
            int randValue = (int) Range(0, normalOptions.size());
            EquipmentOptionsInfoTable equipmentOption = normalOptions.get(randValue);
            selectedOptions.add(equipmentOption);
            normalOptions.remove(randValue);
        }
        for (EquipmentOptionsInfoTable option : selectedOptions) {
            StringMaker.Clear();
            StringMaker.stringBuilder.append(optionsIds);
            if (optionsIds.length() > 0)
                StringMaker.stringBuilder.append(",");
            StringMaker.stringBuilder.append(option.getID());
            optionsIds = StringMaker.stringBuilder.toString();

            StringMaker.Clear();
            StringMaker.stringBuilder.append(optionsValues);
            if (optionsValues.length() > 0)
                StringMaker.stringBuilder.append(",");
            double decideOptionValue = CalculateOptionValue(option.getBaseValue(), level, gradeValue, classValue, option.getPERCENT_OR_FIXEDVALUE());
            StringMaker.stringBuilder.append(decideOptionValue);
            optionsValues = StringMaker.stringBuilder.toString();
        }
        heroEquipment.setOptionIds(optionsIds);
        heroEquipment.setOptionValues(optionsValues);
    }

    //다음 레벨까지 필요한 경험치 계산
    public static int CalculateNeedExp(int nowLv, int grade) {
        if(nowLv <= 0)
            return 0;
        double a = nowLv - 1;
        double b = grade * 0.1f;
        double result = (25 + 25 * a * b) * grade;
        return (int)Math.round(result);
    }

    //장비/강화석에 따른 획득 경험치 기준
    public static int GetBaseExpFromMaterialGrade(String itemCode, String materialGrade) {
        int getExp = 0;
        if(itemCode.contains("enchant")) {
            switch (materialGrade)
            {
                case "Normal":
                    getExp = 30;
                    break;
                case "Rare":
                    getExp = 70;
                    break;
                case "Hero":
                    getExp = 150;
                    break;
                case "Legend":
                    getExp = 300;
                    break;
                case "Divine":
                    getExp = 500;
                    break;
                case "Ancient":
                    getExp = 1000;
                    break;
            }
        }
        else {
            switch (materialGrade)
            {
                case "Normal":
                    getExp = 15;
                    break;
                case "Rare":
                    getExp = 30;
                    break;
                case "Hero":
                    getExp = 60;
                    break;
                case "Legend":
                    getExp = 120;
                    break;
                case "Divine":
                    getExp = 200;
                    break;
                case "Ancient":
                    getExp = 400;
                    break;
            }
        }
        return getExp;
    }
}
