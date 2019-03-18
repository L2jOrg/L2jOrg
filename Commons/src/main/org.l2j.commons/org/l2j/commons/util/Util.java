package org.l2j.commons.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Util {

    public static final String STRING_EMPTY = "";
    public static final int[] INT_ARRAY_EMPTY = new int[0];
    public static final String[] STRING_ARRAY_EMPTY = new String[0];

    public static String printData(byte[] data, int len) {
        StringBuilder result = new StringBuilder();

        int counter = 0;

        for (int i = 0; i < len; i++) {
            if ((counter % 16) == 0) {
                result.append(fillHex(i, 4));
                result.append(": ");
            }

            result.append(fillHex(data[i] & 0xff, 2));
            result.append(" ");
            counter++;
            if (counter == 16) {
                result.append("   ");

                int charpoint = i - 15;
                for (int a = 0; a < 16; a++) {
                    charpoint = appendCharAtPoint(data, result, charpoint);
                }

                result.append("\n");
                counter = 0;
            }
        }

        int rest = data.length % 16;
        if (rest > 0) {
            for (int i = 0; i < (17 - rest); i++) {
                result.append("   ");
            }

            int charpoint = data.length - rest;
            for (int a = 0; a < rest; a++) {
                charpoint = appendCharAtPoint(data, result, charpoint);
            }
            result.append("\n");
        }
        return result.toString();
    }

    private static int appendCharAtPoint(byte[] data, StringBuilder result, int charpoint) {
        int t1 = data[charpoint++];
        if ((t1 > 0x1f) && (t1 < (byte) 0x80)) {
            result.append((char) t1);
        } else {
            result.append('.');
        }
        return charpoint;
    }

    private static String fillHex(int data, int digits) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(data));
        builder.insert(0, "0".repeat(digits));
        return builder.toString();
    }

    public static boolean isNullOrEmpty(CharSequence value) {
        return isNull(value) || value.length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return nonNull(value) && !value.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static String hash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] raw = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(md.digest(raw));
    }
}
