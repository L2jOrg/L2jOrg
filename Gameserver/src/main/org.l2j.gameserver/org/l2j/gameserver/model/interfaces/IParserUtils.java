package org.l2j.gameserver.model.interfaces;

import java.time.Duration;

/**
 * Simple interface for parser, enforces of a fall back value.<br>
 * More suitable for developers not sure about their data.<br>
 *
 * @author xban1x
 */
public interface IParserUtils {
    boolean getBoolean(String key, boolean defaultValue);

    byte getByte(String key, byte defaultValue);

    short getShort(String key, short defaultValue);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    float getFloat(String key, float defaultValue);

    double getDouble(String key, double defaultValue);

    String getString(String key, String defaultValue);

    Duration getDuration(String key, Duration defaultValue);

    <T extends Enum<T>> T getEnum(String key, Class<T> clazz, T defaultValue);
}
