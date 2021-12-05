package org.tonyyan.plugin.documentassistant.utils;

public class StringUtils {

    public static boolean isEmpty(String str) {
        if (str == null || str.length() <= 0) {
            return true;
        }
        return false;
    }

}
