package org.l2j.commons.util;

public class Converter {

    public static int stringToInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long stringToLong(String value, long defaltValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return defaltValue;
        }
    }
}
