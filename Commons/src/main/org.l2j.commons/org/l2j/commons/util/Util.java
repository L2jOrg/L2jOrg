package org.l2j.commons.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Util {

    public static final String STRING_EMPTY = "";
    public static final int[] INT_ARRAY_EMPTY = new int[0];
    public static final String[] STRING_ARRAY_EMPTY = new String[0];

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

    public static Field[] findFieldsAnnoted(Class<?> classToSearch, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<>();
        Class<?> searchClass = classToSearch;
        while (nonNull(searchClass)) {
            fields.addAll(Stream.of(searchClass.getDeclaredFields()).filter(f -> f.isAnnotationPresent(annotationClass)).collect(Collectors.toList()));
            searchClass = searchClass.getSuperclass();
        }
        return fields.toArray(Field[]::new);
    }

    public static List<Field> fieldsOf(Class<?> classToSearch) {
        List<Field> fields = new ArrayList<>();
        Class<?> searchClass = classToSearch;
        while (nonNull(searchClass)) {
            fields.addAll(Stream.of(searchClass.getDeclaredFields()).collect(Collectors.toList()));
            searchClass = searchClass.getSuperclass();
        }
        return Collections.unmodifiableList(fields);
    }
}
