package com.onlyonegames.util;

public class StringMaker {
    static public StringBuilder stringBuilder = new StringBuilder();

    public static void Clear() {
        stringBuilder.delete(0, stringBuilder.length());
    }
}
