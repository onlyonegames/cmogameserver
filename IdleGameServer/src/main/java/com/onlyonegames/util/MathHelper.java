package com.onlyonegames.util;

import java.util.ArrayList;
import java.util.List;

public class MathHelper {

    public static int Clamp(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public static long Clamp(long value, long min, long max) { return Math.min(Math.max(value, min), max);}

    public static float Clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double Clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double Range(double min/*inclusive*/, double max/*exclusive*/) {
        return (Math.random() * ((max - min))) + min;
    }

//    public static double Range(double min, double max) {
//        return (Math.random() * max) + min;
//    }

    public static double Round2(double value) {
        return Math.round(value * 100)/100.0;
    }
    public static double Round3(double value) {
        return Math.round(value * 1000)/1000.0;
    }
    public static double Round4(double value) {
        return Math.round(value * 10000)/10000.0;
    }

    public static double RoundUPMinus1(double value) {
        return RoundUP(value * 0.1) * 10;
    }

    public static double RoundUPMinus2(double value) {
        return RoundUP(value * 0.01) * 100;
    }

    public static double RoundUPMinus3(double value) {
        return RoundUP(value * 0.001) * 1000;
    }

    public static double RoundUP(double value) {
        double total = value + 0.4f;
        return Math.round(total);
    }
    //장비 옵션 계산시 확인 필요함.
    public static double RoundUP2(double value) {
        double total = value + 0.04f;
        return Math.round(total * 100)/100.0;
    }
    static class ProbabilityInfo{
        public int index;
        public Double probability;
    }
    public static int RandomIndexWidthProbability(List<Double> probabilityList) {
        List<ProbabilityInfo> probabilityInfoList = new ArrayList<>();
        double sum = 0;
        int listSize = probabilityList.size();
        for (int i = 0; i < listSize; i++) {
            double probability = probabilityList.get(i);
            sum += probability;
            ProbabilityInfo probabilityInfo = new ProbabilityInfo();
            probabilityInfo.index = i;
            probabilityInfo.probability = probability;

            probabilityInfoList.add(probabilityInfo);
        }
        probabilityInfoList.sort((a,b) -> a.probability.compareTo(b.probability));
        double rand = Range(0, sum);

        double current = 0;
        for (int i = 0; i < listSize; i++) {
            ProbabilityInfo probabilityInfo = probabilityInfoList.get(i);
            current += probabilityInfo.probability;
            if (rand < current) {
                return probabilityInfo.index;
            }
        }
        return 0;
    }
}
