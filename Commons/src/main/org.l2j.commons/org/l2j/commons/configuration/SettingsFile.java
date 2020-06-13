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
package org.l2j.commons.configuration;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Files.newBufferedReader;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.*;

/**
 * @author JoeAlisson
 */
public final class SettingsFile extends Properties {

    private static final long serialVersionUID = -4599023842346938325L;
    private static final Logger logger = LoggerFactory.getLogger(SettingsFile.class);
    private static final String DEFAULT_DELIMITER = "[,;]";

    SettingsFile(String filePath) {
        try (var reader = newBufferedReader(Paths.get(filePath))) {
            load(reader);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public SettingsFile() {
    }

    public String getString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public int getInteger(String key, int defaultValue) {
        return getInteger(key, 10, defaultValue);
    }

    public int getInteger(String key, int radix, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key), radix);
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (isNullOrEmpty(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public List<String> getStringList(String key, String defaultValue, String delimiter) {
        String[] values = getProperty(key, defaultValue).split(delimiter);
        return Stream.of(values).filter(Util::isNotEmpty).collect(Collectors.toList());
    }

    public String[] getStringArray(String key) {
        String value = getProperty(key);
        if(isNullOrEmpty(value)) {
            return STRING_ARRAY_EMPTY;
        }
        var values = value.split(DEFAULT_DELIMITER);
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
        return values;
    }

    public Map<Integer, Integer> getIntegerMap(String key, String entryDelimiter, String valueDelimiter) {
        String[] values = getProperty(key, "").split(entryDelimiter);
        Map<Integer, Integer> map = new HashMap<>();

        Stream.of(values).filter(Util::isNotEmpty).forEach(v -> putInMap(key, valueDelimiter, map, v));
        return map;
    }

    private void putInMap(String key, String valueDelimiter, Map<Integer, Integer> map, String entry) {
        try {
            String[] value = entry.split(valueDelimiter);
            int mapKey = Integer.parseInt(value[0].trim());
            int mapValue = Integer.parseInt(value[1].trim());
            map.put(mapKey, mapValue);
        } catch (Exception e) {
            logger.warn("Error getting property {} on entry {}: {}", key, entry, e.getLocalizedMessage());
        }
    }

    public List<Integer> getIntegerList(String key, String delimiter) {
        String[] values = getProperty(key).split(delimiter);
        List<Integer> list = new ArrayList<>(values.length);
        Stream.of(values).filter(Util::isNotEmpty).forEach(v -> {
            try {
                int value = Integer.parseInt(v.trim());
                list.add(value);
            } catch (Exception e) {
                logger.warn("Error getting property {} on value {} : {}", key, v, e.getLocalizedMessage());
            }
        });
        return list;
    }

    public IntSet getIntSet(String key, String delimiter) {
        return StreamUtil.collectToSet(stream(getProperty(key).split(delimiter)).filter(Util::isInteger).mapToInt(Integer::parseInt));
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public int[] getIntegerArray(String key, String delimiter) {
        var property = getProperty(key);
        if(isNullOrEmpty(property)) {
            return INT_ARRAY_EMPTY;
        }
        var values = property.split(delimiter);
        int[] array = new int[values.length];
        int index = 0;
        for (String v : values) {
            if (!isInteger(v)) {
                continue;
            }
            array[index++] = Integer.parseInt(v);
        }
        return array;
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public byte getByte(String key, byte defaultValue) {
        try {
            return Byte.parseByte(getProperty(key));
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public short getShort(String key, short defaultValue) {
        try {
            return Short.parseShort(getProperty(key));
        } catch (Exception e) {
            logger.warn("Error getting property {} : {}", key, e.getLocalizedMessage());
        }
        return defaultValue;
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        String value;
        if(isNullOrEmpty(value = getProperty(key)) || isNull(enumClass)) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception e) {
            logger.warn("Unknown enum constant {} of type {}", key, enumClass);
        }
        return defaultValue;
    }

    public <T extends Enum<T>> Set<T> getEnumSet(String key, Class<T> enumClass, Set<T> defaultValue) {
        String value;
        if(isNullOrEmpty(value = getProperty(key)) || isNull(enumClass)) {
            return defaultValue;
        }
        var enums = value.split(DEFAULT_DELIMITER);
        var result = EnumSet.noneOf(enumClass);
        for (String enumName : enums) {
            try{
                result.add(Enum.valueOf(enumClass, enumName.trim()));
            } catch (Exception e) {
                logger.warn("Unknown enum constant {} of type {}", enumName, enumClass);
            }
        }
        return result;
    }

    public Duration getDuration(String key, long defaultValue) {
        return getDuration(key, ChronoUnit.SECONDS, defaultValue);
    }

    public Duration getDuration(String key, TemporalUnit unit, long defaultValue) {
        return Duration.of(getLong(key, defaultValue), unit);
    }
}