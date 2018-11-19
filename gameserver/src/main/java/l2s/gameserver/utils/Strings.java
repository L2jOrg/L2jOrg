package l2s.gameserver.utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import l2s.gameserver.Config;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strings
{
	private static final Logger _log = LoggerFactory.getLogger(Strings.class);

	public static String stripSlashes(String s)
	{
		if(s == null)
			return "";
		s = s.replace("\\'", "'");
		s = s.replace("\\\\", "\\");
		return s;
	}

	//TODO вынести этот бред
	public static Boolean parseBoolean(Object x)
	{
		if(x == null)
			return false;

		if(x instanceof Number)
			return ((Number) x).intValue() > 0;

		if(x instanceof Boolean)
			return (Boolean) x;

		if(x instanceof Double)
			return Math.abs((Double) x) < 0.00001;

		return !String.valueOf(x).isEmpty();
	}

	private static String[] tr;
	private static String[] trb;
	private static String[] trcode;

	public static void reload()
	{
		try
		{
			String[] pairs = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/translit.txt")).split("\n");
			tr = new String[pairs.length * 2];
			for(int i = 0; i < pairs.length; i++)
			{
				String[] ss = pairs[i].split(" +");
				tr[i * 2] = ss[0];
				tr[i * 2 + 1] = ss[1];
			}

			pairs = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/translit_back.txt")).split("\n");
			trb = new String[pairs.length * 2];
			for(int i = 0; i < pairs.length; i++)
			{
				String[] ss = pairs[i].split(" +");
				trb[i * 2] = ss[0];
				trb[i * 2 + 1] = ss[1];
			}

			pairs = FileUtils.readFileToString(new File(Config.DATAPACK_ROOT, "data/transcode.txt")).split("\n");
			trcode = new String[pairs.length * 2];
			for(int i = 0; i < pairs.length; i++)
			{
				String[] ss = pairs[i].split(" +");
				trcode[i * 2] = ss[0];
				trcode[i * 2 + 1] = ss[1];
			}
		}
		catch(IOException e)
		{
			_log.error("", e);
		}
		_log.info("Loaded " + (tr.length + tr.length + trcode.length) + " translit entries.");
	}

	public static String translit(String s)
	{
		for(int i = 0; i < tr.length; i += 2)
			s = s.replace(tr[i], tr[i + 1]);

		return s;
	}

	public static String fromTranslit(String s, int type)
	{
		if(type == 1)
			for(int i = 0; i < trb.length; i += 2)
				s = s.replace(trb[i], trb[i + 1]);
		else if(type == 2)
			for(int i = 0; i < trcode.length; i += 2)
				s = s.replace(trcode[i], trcode[i + 1]);

		return s;
	}

	public static String replace(String str, String regex, int flags, String replace)
	{
		return Pattern.compile(regex, flags).matcher(str).replaceAll(replace);
	}

	public static boolean matches(String str, String regex, int flags)
	{
		return Pattern.compile(regex, flags).matcher(str).matches();
	}

	/***
	 * Склеивалка для строк
	 * @param glueStr - строка разделитель, может быть пустой строкой или null
	 * @param strings - массив из строк которые надо склеить
	 * @param startIdx - начальный индекс, если указать отрицательный то он отнимется от количества строк
	 * @param maxCount - мескимум элементов, если 0 - вернутся пустая строка, если отрицательный то учитыватся не будет
	 */
	public static String joinStrings(String glueStr, String[] strings, int startIdx, int maxCount)
	{
		String result = "";
		if(startIdx < 0)
		{
			startIdx += strings.length;
			if(startIdx < 0)
				return result;
		}
		while(startIdx < strings.length && maxCount != 0)
		{
			if(!result.isEmpty() && glueStr != null && !glueStr.isEmpty())
				result += glueStr;
			result += strings[startIdx++];
			maxCount--;
		}
		return result;
	}

	/***
	 * Склеивалка для строк
	 * @param glueStr - строка разделитель, может быть пустой строкой или null
	 * @param strings - массив из строк которые надо склеить
	 * @param startIdx - начальный индекс, если указать отрицательный то он отнимется от количества строк
	 */
	public static String joinStrings(String glueStr, String[] strings, int startIdx)
	{
		return joinStrings(glueStr, strings, startIdx, -1);
	}

	/***
	 * Склеивалка для строк
	 * @param glueStr - строка разделитель, может быть пустой строкой или null
	 * @param strings - массив из строк которые надо склеить
	 */
	public static String joinStrings(String glueStr, String[] strings)
	{
		return joinStrings(glueStr, strings, 0);
	}

	public static String stripToSingleLine(String s)
	{
		if(s.isEmpty())
			return s;
		s = s.replaceAll("\\\\n", "\n");
		int i = s.indexOf("\n");
		if(i > -1)
			s = s.substring(0, i);
		return s;
	}

	/**
	 * @param text - the text to check
	 * @return {@code true} if {@code text} contains only numbers, {@code false} otherwise
	 */
	public static boolean isDigit(String text)
	{
		if(text == null)
			return false;
		return text.matches("[0-9]+");
	}
}