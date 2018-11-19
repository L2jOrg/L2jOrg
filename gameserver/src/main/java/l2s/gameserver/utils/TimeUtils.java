package l2s.gameserver.utils;

import l2s.commons.time.cron.SchedulingPattern;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author VISTALL
 * @date 16:18/14.02.2011
 */
public class TimeUtils
{
	public static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

	public static final SchedulingPattern DAILY_DATE_PATTERN = new SchedulingPattern("30 6 * * *");

	public static String toSimpleFormat(Calendar cal)
	{
		return SIMPLE_FORMAT.format(cal.getTime());
	}

	public static String toSimpleFormat(long cal)
	{
		return SIMPLE_FORMAT.format(cal);
	}

	public static Calendar getCalendarFromString(String datetime, String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		try
		{
			Date time = df.parse(datetime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(time);

			return calendar;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}