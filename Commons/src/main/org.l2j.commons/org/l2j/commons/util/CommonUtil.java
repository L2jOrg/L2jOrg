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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.*;

public final class CommonUtil
{
	private static final char[] ILLEGAL_CHARACTERS =
	{
		'/',
		'\n',
		'\r',
		'\t',
		'\0',
		'\f',
		'`',
		'?',
		'*',
		'\\',
		'<',
		'>',
		'|',
		'\"',
		':'
	};
	
	/**
	 * Method to generate the hexadecimal representation of a byte array.<br>
	 * 16 bytes per row, while ascii chars or "." is shown at the end of the line.
	 * @param data the byte array to be represented in hexadecimal representation
	 * @param len the number of bytes to represent in hexadecimal representation
	 * @return byte array represented in hexadecimal format
	 */
	public static String printData(byte[] data, int len)
	{
		if(data == null || data.length == 0 || len == 0) {
			return Util.STRING_EMPTY;
		}
		return new String(HexUtils.bArr2HexEdChars(data, len));
	}
	
	/**
	 * This call is equivalent to Util.printData(data, data.length)
	 * @see CommonUtil#printData(byte[],int)
	 * @param data data to represent in hexadecimal
	 * @return byte array represented in hexadecimal format
	 */
	public static String printData(byte[] data)
	{
		return printData(data, data.length);
	}

	/**
	 * Replaces most invalid characters for the given string with an underscore.
	 * @param str the string that may contain invalid characters
	 * @return the string with invalid character replaced by underscores
	 */
	public static String replaceIllegalCharacters(String str)
	{
		String valid = str;
		for (char c : ILLEGAL_CHARACTERS)
		{
			valid = valid.replace(c, '_');
		}
		return valid;
	}
	
	/**
	 * Verify if a file name is valid.
	 * @param name the name of the file
	 * @return {@code true} if the file name is valid, {@code false} otherwise
	 */
	public static boolean isValidFileName(String name)
	{
		final File f = new File(name);
		try
		{
			f.getCanonicalPath();
			return true;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	/**
	 * Split words with a space.
	 * @param input the string to split
	 * @return the split string
	 */
	public static String splitWords(String input)
	{
		return input.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
	}

	/**
	 * Method to get the stack trace of a Throwable into a String
	 * @param t Throwable to get the stacktrace from
	 * @return stack trace from Throwable as String
	 */
	public static String getStackTrace(Throwable t)
	{
		final StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static String getTraceString(StackTraceElement[] stackTraceElements)
	{
		final StringJoiner sj = new StringJoiner("\n");
		for (StackTraceElement stackTraceElement : stackTraceElements)
		{
			sj.add(stackTraceElement.toString());
		}
		return sj.toString();
	}
	
	public static int min(int value1, int value2, int... values)
	{
		int min = Math.min(value1, value2);
		for (int value : values)
		{
			if (min > value)
			{
				min = value;
			}
		}
		return min;
	}

	public static int getIndexOfMaxValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] > array[index])
			{
				index = i;
			}
		}
		return index;
	}
	
	public static int getIndexOfMinValue(int... array)
	{
		int index = 0;
		for (int i = 1; i < array.length; i++)
		{
			if (array[i] < array[index])
			{
				index = i;
			}
		}
		return index;
	}

	/**
	 * Re-Maps a value from one range to another.
	 * @param input
	 * @param inputMin
	 * @param inputMax
	 * @param outputMin
	 * @param outputMax
	 * @return The mapped value
	 */
	public static long map(long input, long inputMin, long inputMax, long outputMin, long outputMax)
	{
		input = constrain(input, inputMin, inputMax);
		return (((input - inputMin) * (outputMax - outputMin)) / (inputMax - inputMin)) + outputMin;
	}

	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static int constrain(int input, int min, int max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static long constrain(long input, long min, long max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}
	
	/**
	 * Constrains a number to be within a range.
	 * @param input the number to constrain, all data types
	 * @param min the lower end of the range, all data types
	 * @param max the upper end of the range, all data types
	 * @return input: if input is between min and max, min: if input is less than min, max: if input is greater than max
	 */
	public static double constrain(double input, double min, double max)
	{
		return (input < min) ? min : (input > max) ? max : input;
	}

	/**
	 * @param <T>
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static <T> boolean contains(T[] array, T obj)
	{
		for (T element : array)
		{
			if (element.equals(obj))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param array - the array to look into
	 * @param obj - the object to search for
	 * @param ignoreCase
	 * @return {@code true} if the {@code array} contains the {@code obj}, {@code false} otherwise.
	 */
	public static boolean contains(String[] array, String obj, boolean ignoreCase)
	{
		for (String element : array)
		{
			if (element.equals(obj) || (ignoreCase && element.equalsIgnoreCase(obj)))
			{
				return true;
			}
		}
		return false;
	}
	
	public static int parseInt(String value, int defaultValue)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	/**
	 * @param str - the string whose first letter to capitalize
	 * @return a string with the first letter of the {@code str} capitalized
	 */
	public static String capitalizeFirst(String str)
	{
		if ((str == null) || str.isEmpty())
		{
			return str;
		}
		final char[] arr = str.toCharArray();
		final char c = arr[0];
		
		if (Character.isLetter(c))
		{
			arr[0] = Character.toUpperCase(c);
		}
		return new String(arr);
	}
	
	/**
	 * Based on implode() in PHP
	 * @param <T>
	 * @param iteratable
	 * @param delim
	 * @return a delimited string for a given array of string elements.
	 */
	public static <T> String implode(Iterable<T> iteratable, String delim)
	{
		final StringJoiner sj = new StringJoiner(delim);
		iteratable.forEach(o -> sj.add(o.toString()));
		return sj.toString();
	}
	
	/**
	 * Based on implode() in PHP
	 * @param <T>
	 * @param array
	 * @param delim
	 * @return a delimited string for a given array of string elements.
	 */
	public static <T> String implode(T[] array, String delim)
	{
		final StringJoiner sj = new StringJoiner(delim);
		for (T o : array)
		{
			sj.add(o.toString());
		}
		return sj.toString();
	}

}
