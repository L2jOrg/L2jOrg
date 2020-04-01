package org.l2j.commons.util;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;

import java.util.EnumSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamUtil {

    public static IntSet collectToSet(IntStream stream) {
        return stream.collect(HashIntSet::new, IntSet::add, IntSet::addAll);
    }

    public static <E extends Enum<E>> EnumSet<E> collectToEnumSet(Class<E> enumClass, Stream<E> stream) {
        return stream.collect(() -> EnumSet.noneOf(enumClass), EnumSet::add, EnumSet::addAll);
    }
}
