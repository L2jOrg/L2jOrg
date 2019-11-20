package org.l2j.commons.util;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;

import java.util.stream.IntStream;

public class StreamUtil {

    public static IntSet collectToSet(IntStream stream) {
        return stream.collect(HashIntSet::new, IntSet::add, IntSet::addAll);
    }
}
