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

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;

import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamUtil {

    public static IntSet collectToSet(IntStream stream) {
        return stream.collect(HashIntSet::new, IntSet::add, IntSet::addAll);
    }

    public static <E extends Enum<E>> EnumSet<E> collectToEnumSet(Class<E> enumClass, Stream<E> stream) {
        return stream.collect(() -> EnumSet.noneOf(enumClass), EnumSet::add, EnumSet::addAll);
    }

    public static <T> IntMap<T> collectToMap(Stream<IntMap.Entry<T>> stream ) {
        return stream.filter(Objects::nonNull).collect(HashIntMap::new,  (map, entry) -> map.put(entry.getKey(), entry.getValue()) , IntMap::putAll);
    }
}