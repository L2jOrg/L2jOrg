/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.commons.util;

import io.github.joealisson.primitive.IntCollection;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class Util {

    public static final String STRING_EMPTY = "";
    public static final String SPACE = " ";
    public static final int[] INT_ARRAY_EMPTY = new int[0];
    public static final byte[] BYTE_ARRAY_EMPTY = new byte[0];
    public static final String[] STRING_ARRAY_EMPTY = new String[0];
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean isNullOrEmpty(final CharSequence value) {
        return isNull(value) || value.length() == 0;
    }

    public static boolean isNotEmpty(final String value) {
        return nonNull(value) && !value.isBlank();
    }

    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(int[] data) {
        return isNull(data) || data.length == 0;
    }

    public static <T> int zeroIfNullOrElse(T obj, ToIntFunction<T> function) {
        return isNull(obj) ? 0 : function.applyAsInt(obj);
    }

    public static <T> boolean falseIfNullOrElse(T obj, Predicate<T> predicate) {
        return nonNull(obj) && predicate.test(obj);
    }

    public static <T, R> Set<R> emptySetIfNullOrElse(T obj, Function<T, Set<R>> function) {
        return isNull(obj) ? Collections.emptySet() : function.apply(obj);
    }

    public static <T, R> Collection<R> emptyListIfNullOrElse(T obj, Function<T, Collection<R>> function) {
        return isNull(obj) ?  Collections.emptyList() : function.apply(obj);
    }

   public static <T> String emptyIfNullOrElse(T obj, Function<T, String> function) {
        return isNull(obj) ? STRING_EMPTY : function.apply(obj);
    }

    public static <T, R> R computeIfNonNull(T obj, Function<T, R> function) {
        return isNull(obj) ? null : function.apply(obj);
    }

    public static <T> void doIfNonNull(T obj, Consumer<T> action) {
        if(nonNull(obj)) {
            action.accept(obj);
        }
    }

    public static boolean contains(int[] array, int obj) {
        for (int element : array) {
            if (element == obj) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T obj) {
        for (var element : array) {
            if(Objects.equals(element, obj)) {
                return true;
            }
        }
        return false;
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

    public static boolean isDigit(String text) {
        if (isNullOrEmpty(text)) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFloat(final String value) {
        return isNumeric(value, true);
    }

    public static boolean isInteger(final String value) {
        return isNumeric(value, false);
    }

    public static boolean isNumeric(final String value, boolean includePoint) {
        if(isNullOrEmpty(value)) {
            return false;
        }

        if(value.charAt(value.length() -1) == '.' && !includePoint) {
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
                if(!includePoint) {
                    return false;
                }
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

    public static boolean isAlphaNumeric(String text) {
        if ((text == null) || text.isEmpty()) {
            return false;
        }
        for (char c : text.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
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
        return fields;
    }

    public static Field findField(final Class<?> classToSearch, String fieldName) {
        Class<?> searchClass = classToSearch;
        Field field = null;
        while (nonNull(searchClass)) {
            try {
                if(nonNull( field = searchClass.getDeclaredField(fieldName))) {
                    break;
                }
                searchClass = searchClass.getSuperclass();
            } catch (NoSuchFieldException e) {
                break;
            }
        }
        return field;
    }


    public static boolean isAnyNull(Object... objects) {
        for (Object object : objects) {
            if(isNull(object)) {
                return true;
            }
        }
        return false;
    }

    public static int parseNextInt(StringTokenizer st, int defaultVal) {
        if (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (isInteger(token)) {
                return Integer.parseInt(token);
            }
        }
        return defaultVal;
    }

    public static boolean isBetween(int number, int min, int max) {
        return  number >= min && number <= max;
    }

    public static boolean isNotEmpty(IntCollection collection) {
        return nonNull(collection) && collection.size() > 0;
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_DATE);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
