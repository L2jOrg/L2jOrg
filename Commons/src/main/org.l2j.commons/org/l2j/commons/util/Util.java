package org.l2j.commons.util;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Util {

    public static final String STRING_EMPTY = "";
    public static final int[] INT_ARRAY_EMPTY = new int[0];
    public static final String[] STRING_ARRAY_EMPTY = new String[0];

    public static boolean isNullOrEmpty(final CharSequence value) {
        return isNull(value) || value.length() == 0;
    }

    public static boolean isNotEmpty(final String value) {
        return nonNull(value) && !value.isEmpty();
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static <T> int  zeroIfNullElseCompute(T obj, ToIntFunction<T> action) {
        return isNull(obj) ? 0 : action.applyAsInt(obj);
    }

    public static String hash(final String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] raw = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(md.digest(raw));
    }

    public static int hashIp(String ip)  {
        final String[] rawByte = ip.split("\\.");
        final int[] rawIp = new int[4];
        for (int i = 0; i < 4; i++) {
            rawIp[i] = Integer.parseInt(rawByte[i]);
        }

        return rawIp[0] | (rawIp[1] << 8) | (rawIp[2] << 16) | (rawIp[3] << 24);
    }


    public static boolean isNumeric(final String value) {
        if(isNullOrEmpty(value)) {
            return false;
        }

        if(value.charAt(value.length() -1) == '.') {
            return false;
        }

        var beginIndex = 0;
        if(value.charAt(0) == '-') {
            if(value.length() == 1) {
                return false;
            }
            beginIndex = 1;
        }

        var points = 0;

        for(var i = beginIndex; i < value.length(); i++) {
            var caracter = value.charAt(i);
            final var isPoint = caracter == '.';
            if(isPoint) {
                points++;
            }

            if(points > 1) {
                return false;
            }

            if(!isPoint && !Character.isDigit(caracter)) {
                return false;
            }
        }
        return true;
    }

    public static List<Field> fieldsOf(final Class<?> classToSearch) {
        List<Field> fields = new ArrayList<>();
        Class<?> searchClass = classToSearch;
        while (nonNull(searchClass)) {
            fields.addAll(Stream.of(searchClass.getDeclaredFields()).collect(Collectors.toList()));
            searchClass = searchClass.getSuperclass();
        }
        return Collections.unmodifiableList(fields);
    }

}
