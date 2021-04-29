/*
 * Copyright © 2019-2021 L2JOrg
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

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.FileUtil;
import org.l2j.commons.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.*;

/**
 * @author JoeAlisson
 */
public final class SettingsFile extends Properties {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsFile.class);
    private static final String DEFAULT_DELIMITER = "[,;]";
    public static final String ERROR_GETTING_PROPERTY = "Error getting property {} : {}";

    SettingsFile(String filePath) {
        try (var reader = FileUtil.reader(filePath)) {
            load(reader);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public SettingsFile() {
    }

    public String getString(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return getInt(key, 10, defaultValue);
    }

    public int getInt(String key, int radix, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key), radix);
        } catch (Exception e) {
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
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

    public IntSet getIntSet(String key) {
        return getIntSet(key, DEFAULT_DELIMITER);
    }

    public IntSet getIntSet(String key, String delimiter) {
        String value = getProperty(key);
        if(isNullOrEmpty(value)) {
            return Containers.emptyIntSet();
        }

        IntSet set = new HashIntSet();
        var values = value.split(delimiter);
        for (String s : values) {
            if (Util.isInteger(s)) {
                set.add(Integer.parseInt(s));
            }
        }
        return set;
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception e) {
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
        }
        return defaultValue;
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(getProperty(key));
        } catch (Exception e) {
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
        }
        return defaultValue;
    }

    public int[] getIntArray(String key) {
        return getIntArray(key, DEFAULT_DELIMITER);
    }

    public int[] getIntArray(String key, String delimiter) {
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
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
        }
        return defaultValue;
    }

    public byte getByte(String key, byte defaultValue) {
        try {
            return Byte.parseByte(getProperty(key));
        } catch (Exception e) {
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
        }
        return defaultValue;
    }

    public short getShort(String key, short defaultValue) {
        try {
            return Short.parseShort(getProperty(key));
        } catch (Exception e) {
            LOGGER.warn(ERROR_GETTING_PROPERTY, key, e.getMessage());
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
            LOGGER.warn("Unknown enum constant {} of type {}", key, enumClass);
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
                LOGGER.warn("Unknown enum constant {} of type {}", enumName, enumClass);
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

    public Duration parseDuration(String key, String defaultValue) {
        return Duration.parse(getString(key, defaultValue));
    }
}