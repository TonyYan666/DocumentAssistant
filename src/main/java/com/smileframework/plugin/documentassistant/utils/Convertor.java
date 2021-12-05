package com.smileframework.plugin.documentassistant.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Convertor {

    public static String Camel2Underline(String camel) {
        Set<String> changeHistory = new HashSet<>();
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(camel);
        while (matcher.find()) {
            String w = matcher.group().trim();
            if (!changeHistory.contains(w)) {
                camel = camel.replace(w, "_" + w);
                changeHistory.add(w);
            }
        }
        return camel.toUpperCase();
    }

    public static String getFieldGetterName(String fieldName) {
        String result = "get";
        result += fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
        return result;
    }

    public static String getFieldBoolGetterName(String fieldName) {
        String result = "is";
        if (fieldName.startsWith("is")) {
            fieldName = fieldName.substring(2);
        }
        result += fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
        return result;
    }

    public static String getGetterFieldName(String methodName) {
        String result = methodName.substring(3, methodName.length());
        result = result.substring(0, 1).toLowerCase() + result.substring(1, result.length());
        return result;
    }

    public static boolean isFieldGetter(String methodName) {
        if (methodName == null || methodName.trim().isEmpty()) {
            return false;
        }
        if (methodName.indexOf("get") == 1) {
            return true;
        }
        return false;
    }
}