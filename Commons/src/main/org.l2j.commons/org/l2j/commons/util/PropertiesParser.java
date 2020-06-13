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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


/**
 * Simplifies loading of property files and adds logging if a non existing property is requested.
 * @author NosBit
 */
public final class PropertiesParser
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesParser.class);
	
	private final Properties _properties = new Properties();
	private final File _file;
	
	public PropertiesParser(String name)
	{
		this(new File(name));
	}
	
	public PropertiesParser(File file)
	{
		_file = file;
		try (FileInputStream fileInputStream = new FileInputStream(file))
		{
			try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.defaultCharset()))
			{
				_properties.load(inputStreamReader);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("[" + _file.getName() + "] There was an error loading config reason: " + e.getMessage());
		}
	}
	
	public boolean containskey(String key)
	{
		return _properties.containsKey(key);
	}
	
	private String getValue(String key)
	{
		final String value = _properties.getProperty(key);
		return value != null ? value.trim() : null;
	}
	
	public boolean getBoolean(String key, boolean defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
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
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"boolean\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public byte getByte(String key, byte defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Byte.parseByte(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"byte\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public short getShort(String key, short defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Short.parseShort(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"short\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public int getInt(String key, int defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"int\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public long getLong(String key, long defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"long\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public float getFloat(String key, float defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"float\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public double getDouble(String key, double defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"double\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public String getString(String key, String defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		return value;
	}
	
	public <T extends Enum<T>> T getEnum(String key, Class<T> clazz, T defaultValue)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Enum.valueOf(clazz, value);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
			return defaultValue;
		}
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
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + durationPattern + " specified value: " + value + " should be time patttern using default value: " + defaultValue);
		}
		return defaultDuration;
	}
	
	/**
	 * @param key
	 * @param separator
	 * @param defaultValues
	 * @return int array
	 */
	public int[] getIntArray(String key, String separator, int... defaultValues)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValues);
			return defaultValues;
		}
		
		try
		{
			final String[] data = value.trim().split(separator);
			final int[] result = new int[data.length];
			for (int i = 0; i < data.length; i++)
			{
				result[i] = Integer.decode(data[i].trim());
			}
			return result;
		}
		catch (Exception e)
		{
			LOGGER.warn("[+_file.getName()+] Invalid value specified for key: " + key + " specified value: " + value + " should be array using default value: " + defaultValues);
			return defaultValues;
		}
	}
	
	/**
	 * @param <T>
	 * @param key
	 * @param separator
	 * @param clazz
	 * @param defaultValues
	 * @return enum array
	 */
	@SafeVarargs
	public final <T extends Enum<T>> T[] getEnumArray(String key, String separator, Class<T> clazz, T... defaultValues)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValues);
			return defaultValues;
		}
		
		try
		{
			final String[] data = value.trim().split(separator);
			@SuppressWarnings("unchecked")
			final T[] result = (T[]) Array.newInstance(clazz, data.length);
			for (int i = 0; i < data.length; i++)
			{
				result[i] = Enum.valueOf(clazz, data[i]);
			}
			return result;
		}
		catch (Exception e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be array using default value: " + defaultValues);
			return defaultValues;
		}
	}
	
	/**
	 * @param <T>
	 * @param key
	 * @param separator
	 * @param clazz
	 * @param defaultValues
	 * @return list
	 */
	@SafeVarargs
	public final <T extends Enum<T>> List<T> getEnumList(String key, String separator, Class<T> clazz, T... defaultValues)
	{
		final String value = getValue(key);
		if (value == null)
		{
			LOGGER.warn("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValues);
			return Arrays.asList(defaultValues);
		}
		
		try
		{
			final String[] data = value.trim().split(separator);
			final List<T> result = new ArrayList<>(data.length);
			for (String element : data)
			{
				result.add(Enum.valueOf(clazz, element));
			}
			return result;
		}
		catch (Exception e)
		{
			LOGGER.warn("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be array using default value: " + defaultValues);
			return Arrays.asList(defaultValues);
		}
	}
}
