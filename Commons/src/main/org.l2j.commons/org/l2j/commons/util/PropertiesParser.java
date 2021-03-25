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
package org.l2j.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;


/**
 * Simplifies loading of property files and adds logging if a non existing property is requested.
 * @author NosBit
 */
public final class PropertiesParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesParser.class);
	
	private final Properties properties = new Properties();
	private final String filePath;

	public PropertiesParser(String filePath) {
		this.filePath = filePath;
		try(var reader = FileUtil.reader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			LOGGER.warn("[{}] There was an error loading config reason: ", filePath, e);
		}
	}
	
	public boolean containskey(String key)
	{
		return properties.containsKey(key);
	}
	
	private String getValue(String key)
	{
		final String value = properties.getProperty(key);
		return value != null ? value.trim() : null;
	}
	
	public boolean getBoolean(String key, boolean defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		if (value.equalsIgnoreCase("true"))
		{
			return true;
		}
		else if (value.equalsIgnoreCase("false"))
		{
			return false;
		}
		else
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"boolean\" using default value: ", filePath, key, defaultValue);
			return defaultValue;
		}
	}
	
	public byte getByte(String key, byte defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Byte.parseByte(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"byte\" using default value: {}", filePath, key, value, defaultValue);
			return defaultValue;
		}
	}

	public int getInt(String key, int defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"int\" using default value: {}", filePath, key, value, defaultValue);
			return defaultValue;
		}
	}
	
	public long getLong(String key, long defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"long\" using default value: {}", filePath, key, value, defaultValue);
			return defaultValue;
		}
	}
	
	public float getFloat(String key, float defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"float\" using default value: {}", filePath, key, value, defaultValue);
			return defaultValue;
		}
	}
	
	public double getDouble(String key, double defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be \"double\" using default value: {}", filePath, key, value, defaultValue);
			return defaultValue;
		}
	}
	
	public String getString(String key, String defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[{}] missing property for key: {} using default value: {}", filePath, key, defaultValue);
			return defaultValue;
		}
		return value;
	}

	/**
	 * @param durationPattern
	 * @param defaultValue
	 * @return {@link Duration} object by the durationPattern specified, {@code null} in case of malformed pattern.
	 */
	public Duration getDuration(String durationPattern, String defaultValue)
	{
		return getDuration(durationPattern, defaultValue, null);
	}
	
	/**
	 * @param durationPattern
	 * @param defaultValue
	 * @param defaultDuration
	 * @return {@link Duration} object by the durationPattern specified, the defaultDuration in case of malformed pattern.
	 */
	public Duration getDuration(String durationPattern, String defaultValue, Duration defaultDuration)
	{
		final String value = getString(durationPattern, defaultValue);
		try
		{
			return TimeUtil.parseDuration(value);
		}
		catch (IllegalStateException e)
		{
			LOGGER.warn("[{}] Invalid value specified for key: {} specified value: {} should be time patttern using default value: {}", filePath, durationPattern, value, defaultValue);
		}
		return defaultDuration;
	}

}
