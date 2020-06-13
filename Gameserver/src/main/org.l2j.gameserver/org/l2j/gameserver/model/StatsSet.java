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
package org.l2j.gameserver.model;

import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.TimeUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.holders.MinionHolder;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.interfaces.IParserAdvUtils;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.l2j.commons.util.Util.isInteger;

/**
 * This class is meant to hold a set of (key,value) pairs.<br>
 * They are stored as object but can be retrieved in any type wanted. As long as cast is available.<br>
 *
 * @author mkizub
 */
public class StatsSet implements IParserAdvUtils {
    /**
     * Static empty immutable map, used to avoid multiple null checks over the source.
     */
    public static final StatsSet EMPTY_STATSET = new StatsSet(Collections.emptyMap());
    private static final Logger LOGGER = LoggerFactory.getLogger(StatsSet.class);
    private final Map<String, Object> _set;

    public StatsSet() {
        this(ConcurrentHashMap::new);
    }

    public StatsSet(Supplier<Map<String, Object>> mapFactory) {
        this(mapFactory.get());
    }

    public StatsSet(Map<String, Object> map) {
        _set = map;
    }

    public StatsSet(StatsSet other) {
        this();
        merge(other);
    }

    public static StatsSet valueOf(String key, Object value) {
        final StatsSet set = new StatsSet();
        set.set(key, value);
        return set;
    }

    /**
     * Returns the set of values
     *
     * @return HashMap
     */
    public final Map<String, Object> getSet() {
        return _set;
    }

    /**
     * Add a set of couple values in the current set
     *
     * @param newSet : StatsSet pointing out the list of couples to add in the current set
     */
    public void merge(StatsSet newSet) {
        _set.putAll(newSet.getSet());
    }

    public void merge(Map<String, Object> map) {
        _set.putAll(map);
    }

    /**
     * Verifies if the stat set is empty.
     *
     * @return {@code true} if the stat set is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return _set.isEmpty();
    }

    /**
     * Return the boolean value associated with key.
     *
     * @param key : String designating the key in the set
     * @return boolean : value associated to the key
     * @throws IllegalArgumentException : If value is not set or value is not boolean
     */
    @Override
    public boolean getBoolean(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Boolean value required, but not specified");
        }
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        try {
            return Boolean.parseBoolean((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Boolean value required, but found: " + val);
        }
    }

    /**
     * Return the boolean value associated with key.<br>
     * If no value is associated with key, or type of value is wrong, returns defaultValue.
     *
     * @param key : String designating the key in the entry set
     * @return boolean : value associated to the key
     */
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Boolean) {
            return ((Boolean) val).booleanValue();
        }
        try {
            return Boolean.parseBoolean((String) val);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public byte getByte(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).byteValue();
        }
        try {
            return Byte.parseByte((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Byte value required, but found: " + val);
        }
    }

    @Override
    public byte getByte(String key, byte defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).byteValue();
        }
        try {
            return Byte.parseByte((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Byte value required, but found: " + val);
        }
    }

    public short increaseByte(String key, byte increaseWith) {
        final byte newValue = (byte) (getByte(key) + increaseWith);
        set(key, newValue);
        return newValue;
    }

    public short increaseByte(String key, byte defaultValue, byte increaseWith) {
        final byte newValue = (byte) (getByte(key, defaultValue) + increaseWith);
        set(key, newValue);
        return newValue;
    }

    public byte[] getByteArray(String key, String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Byte value required, but not specified");
        }
        if (val instanceof Number) {
            return new byte[]
                    {
                            ((Number) val).byteValue()
                    };
        }
        int c = 0;
        final String[] vals = ((String) val).split(splitOn);
        final byte[] result = new byte[vals.length];
        for (String v : vals) {
            try {
                result[c++] = Byte.parseByte(v);
            } catch (Exception e) {
                throw new IllegalArgumentException("Byte value required, but found: " + val);
            }
        }
        return result;
    }

    public List<Byte> getByteList(String key, String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final List<Byte> result = new ArrayList<>();
        for (Byte i : getByteArray(key, splitOn)) {
            result.add(i);
        }
        return result;
    }

    @Override
    public short getShort(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Short value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }
        if(val instanceof String) {
            var stringVal =  (String) val;
            if(stringVal.contains(".")) {
                stringVal = stringVal.substring(0, stringVal.indexOf("."));
            }
            if(isInteger(stringVal)) {
                return Short.parseShort(stringVal);
            }
        }
        throw new IllegalArgumentException("Short value required, but found: " + val);
    }

    @Override
    public short getShort(String key, short defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).shortValue();
        }
        try {
            return Short.parseShort((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Short value required, but found: " + val);
        }
    }

    public short increaseShort(String key, short increaseWith) {
        final short newValue = (short) (getShort(key) + increaseWith);
        set(key, newValue);
        return newValue;
    }

    public short increaseShort(String key, short defaultValue, short increaseWith) {
        final short newValue = (short) (getShort(key, defaultValue) + increaseWith);
        set(key, newValue);
        return newValue;
    }

    @Override
    public int getInt(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Integer value required, but not specified: " + key + "!");
        }

        return parseInt(val);
    }

    private int parseInt(Object val) {
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }

        if (val instanceof String) {
            var stringVal = (String) val;
            if(stringVal.contains(".")) {
                stringVal = stringVal.substring(0, stringVal.indexOf("."));
            }

            if (isInteger(stringVal)) {
                return Integer.parseInt(stringVal);
            }
        }
        throw new IllegalArgumentException("Integer value required, but found: " + val);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return parseInt(val);
    }

    public int increaseInt(String key, int increaseWith) {
        final int newValue = getInt(key) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    public int increaseInt(String key, int defaultValue, int increaseWith) {
        final int newValue = getInt(key, defaultValue) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    public int[] getIntArray(String key, String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Integer value required, but not specified");
        }
        if (val instanceof Number) {
            return new int[]
                    {
                            ((Number) val).intValue()
                    };
        }
        int c = 0;
        final String[] vals = ((String) val).split(splitOn);
        final int[] result = new int[vals.length];
        for (String v : vals) {
            try {
                result[c++] = Integer.parseInt(v);
            } catch (Exception e) {
                throw new IllegalArgumentException("Integer value required, but found: " + val);
            }
        }
        return result;
    }

    public List<Integer> getIntegerList(String key, String splitOn) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(splitOn);
        final List<Integer> result = new ArrayList<>();
        for (int i : getIntArray(key, splitOn)) {
            result.add(i);
        }
        return result;
    }

    @Override
    public long getLong(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Long value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        try {
            return Long.parseLong((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Long value required, but found: " + val);
        }
    }

    @Override
    public long getLong(String key, long defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        try {
            return Long.parseLong((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Long value required, but found: " + val);
        }
    }

    public long increaseLong(String key, long increaseWith) {
        final long newValue = getLong(key) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    public long increaseLong(String key, long defaultValue, long increaseWith) {
        final long newValue = getLong(key, defaultValue) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    @Override
    public float getFloat(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Float value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).floatValue();
        }
        try {
            return Float.parseFloat((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Float value required, but found: " + val);
        }
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).floatValue();
        }
        try {
            return Float.parseFloat((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Float value required, but found: " + val);
        }
    }

    public float increaseFloat(String key, float increaseWith) {
        final float newValue = getFloat(key) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    public float increaseFloat(String key, float defaultValue, float increaseWith) {
        final float newValue = getFloat(key, defaultValue) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    @Override
    public double getDouble(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Double value required, but not specified");
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        try {
            return Double.parseDouble((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Double value required, but found: " + val);
        }
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        try {
            return Double.parseDouble((String) val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Double value required, but found: " + val);
        }
    }

    public double increaseDouble(String key, double increaseWith) {
        final double newValue = getDouble(key) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    public double increaseDouble(String key, double defaultValue, double increaseWith) {
        final double newValue = getDouble(key, defaultValue) + increaseWith;
        set(key, newValue);
        return newValue;
    }

    @Override
    public String getString(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("String value required, but not specified");
        }
        return String.valueOf(val);
    }

    @Override
    public String getString(String key, String defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return String.valueOf(val);
    }

    @Override
    public Duration getDuration(String key) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("String value required, but not specified");
        }
        return TimeUtil.parseDuration(String.valueOf(val));
    }

    @Override
    public Duration getDuration(String key, Duration defaultValue) {
        Objects.requireNonNull(key);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        return TimeUtil.parseDuration(String.valueOf(val));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(enumClass);
        final Object val = _set.get(key);
        if (val == null) {
            throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but not specified");
        }
        if (enumClass.isInstance(val)) {
            return (T) val;
        }
        try {
            return Enum.valueOf(enumClass, String.valueOf(val));
        } catch (Exception e) {
            throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but found: " + val);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(enumClass);
        final Object val = _set.get(key);
        if (val == null) {
            return defaultValue;
        }
        if (enumClass.isInstance(val)) {
            return (T) val;
        }
        try {
            return Enum.valueOf(enumClass, String.valueOf(val));
        } catch (Exception e) {
            throw new IllegalArgumentException("Enum value of type " + enumClass.getName() + " required, but found: " + val);
        }
    }

    @SuppressWarnings("unchecked")
    public final <A> A getObject(String name, Class<A> type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        final Object obj = _set.get(name);
        if ((obj == null) || !type.isAssignableFrom(obj.getClass())) {
            return null;
        }

        return (A) obj;
    }

    @SuppressWarnings("unchecked")
    public final <A> A getObject(String name, Class<A> type, A defaultValue) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        final Object obj = _set.get(name);
        if ((obj == null) || !type.isAssignableFrom(obj.getClass())) {
            return defaultValue;
        }

        return (A) obj;
    }

    public SkillHolder getSkillHolder(String key) {
        Objects.requireNonNull(key);
        final Object obj = _set.get(key);
        if (!(obj instanceof SkillHolder)) {
            return null;
        }

        return (SkillHolder) obj;
    }

    public Location getLocation(String key) {
        Objects.requireNonNull(key);
        final Object obj = _set.get(key);
        if (!(obj instanceof Location)) {
            return null;
        }
        return (Location) obj;
    }

    @SuppressWarnings("unchecked")
    public List<MinionHolder> getMinionList(String key) {
        Objects.requireNonNull(key);
        final Object obj = _set.get(key);
        if (!(obj instanceof List<?>)) {
            return Collections.emptyList();
        }

        return (List<MinionHolder>) obj;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(clazz);
        final Object obj = _set.get(key);
        if (!(obj instanceof List<?>)) {
            return null;
        }

        final List<Object> originalList = (List<Object>) obj;
        if (!originalList.isEmpty() && !originalList.stream().allMatch(clazz::isInstance)) {
            if (clazz.getSuperclass() == Enum.class) {
                throw new IllegalAccessError("Please use getEnumList if you want to get list of Enums!");
            }

            // Attempt to convert the list
            final List<T> convertedList = convertList(originalList, clazz);
            if (convertedList == null) {
                LOGGER.warn("getList(\"" + key + "\", " + clazz.getSimpleName() + ") requested with wrong generic type: " + obj.getClass().getGenericInterfaces()[0] + "!", new ClassNotFoundException());
                return null;
            }

            // Overwrite the existing list with proper generic type
            _set.put(key, convertedList);
            return convertedList;
        }
        return (List<T>) obj;
    }

    public <T> List<T> getList(String key, Class<T> clazz, List<T> defaultValue) {
        final List<T> list = getList(key, clazz);
        return list == null ? defaultValue : list;
    }

    public <T extends Enum<T>> EnumSet<T> getStringAsEnumSet(String key, Class<T> enumClass) {
        return getStringAsEnumSet(key, enumClass, Util.SPACE);
    }

    public <T extends Enum<T>> EnumSet<T> getStringAsEnumSet(String key, Class<T> enumClass, String delimiter) {
        var values = getString(key);
        if(Util.isNotEmpty(values)) {
            try {
              return StreamUtil.collectToEnumSet(enumClass, Arrays.stream(values.split(delimiter)).map(e -> Enum.valueOf(enumClass, e)));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return EnumSet.noneOf(enumClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> List<T> getEnumList(String key, Class<T> clazz) {
        final Object obj = _set.get(key);
        if (!(obj instanceof List<?>)) {
            return null;
        }

        final List<Object> originalList = (List<Object>) obj;
        if (!originalList.isEmpty() && (obj.getClass().getGenericInterfaces()[0] != clazz) && originalList.stream().allMatch(name -> GameUtils.isEnum(name.toString(), clazz))) {
            final List<T> convertedList = originalList.stream().map(Object::toString).map(name -> Enum.valueOf(clazz, name)).map(clazz::cast).collect(Collectors.toList());

            // Overwrite the existing list with proper generic type
            _set.put(key, convertedList);
            return convertedList;
        }
        return (List<T>) obj;
    }

    /**
     * @param <T>
     * @param originalList
     * @param clazz
     * @return
     */
    private <T> List<T> convertList(List<Object> originalList, Class<T> clazz) {
        if (clazz == Integer.class) {
            if (originalList.stream().map(Object::toString).allMatch(Util::isInteger)) {
                return originalList.stream().map(Object::toString).map(Integer::valueOf).map(clazz::cast).collect(Collectors.toList());
            }
        } else if (clazz == Float.class) {
            if (originalList.stream().map(Object::toString).allMatch(Util::isFloat)) {
                return originalList.stream().map(Object::toString).map(Float::valueOf).map(clazz::cast).collect(Collectors.toList());
            }
        } else if (clazz == Double.class) {
            if (originalList.stream().map(Object::toString).allMatch(Util::isFloat)) {
                return originalList.stream().map(Object::toString).map(Double::valueOf).map(clazz::cast).collect(Collectors.toList());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass) {
        final Object obj = _set.get(key);
        if ((obj == null) || !(obj instanceof Map<?, ?>)) {
            return null;
        }

        final Map<?, ?> originalList = (Map<?, ?>) obj;
        if (!originalList.isEmpty()) {
            if ((!originalList.keySet().stream().allMatch(keyClass::isInstance)) || (!originalList.values().stream().allMatch(valueClass::isInstance))) {
                LOGGER.warn("getMap(\"" + key + "\", " + keyClass.getSimpleName() + ", " + valueClass.getSimpleName() + ") requested with wrong generic type: " + obj.getClass().getGenericInterfaces()[0] + "!", new ClassNotFoundException());
            }
        }
        return (Map<K, V>) obj;
    }

    public StatsSet set(String name, Object value) {
        if (value == null) {
            return this;
        }
        _set.put(name, value);
        return this;
    }

    public StatsSet set(String key, boolean value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, byte value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, short value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, int value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, long value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, float value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, double value) {
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, String value) {
        if (value == null) {
            return this;
        }
        _set.put(key, value);
        return this;
    }

    public StatsSet set(String key, Enum<?> value) {
        if (value == null) {
            return this;
        }
        _set.put(key, value);
        return this;
    }

    public void remove(String key) {
        _set.remove(key);
    }

    public boolean contains(String name) {
        return _set.containsKey(name);
    }

    @Override
    public String toString() {
        return "StatsSet{_set=" + _set + '}';
    }
}
