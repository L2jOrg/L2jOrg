package l2s.commons.time.cron;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AddPattern implements NextTime
{
	private int monthInc = -1;
	private int monthSet = -1;
	private int dayOfMonthInc = -1;
	private int dayOfMonthSet = -1;
	private int hourOfDayInc = -1;
	private int hourOfDaySet = -1;
	private int minuteInc = -1;
	private int minuteSet = -1;

	public AddPattern(String pattern)
	{
		String[] parts = pattern.split("\\s+");
		if(parts.length == 2)
		{
			String datepartsstr = parts[0];
			String[] dateparts = datepartsstr.split(":");
			if(dateparts.length == 2)
			{
				if(dateparts[0].startsWith("+"))
					monthInc = Integer.parseInt(dateparts[0].substring(1));
				else
					monthSet = Integer.parseInt(dateparts[0]) - 1;
			}
			String datemodstr = dateparts[dateparts.length - 1];
			if(datemodstr.startsWith("+"))
				dayOfMonthInc = Integer.parseInt(datemodstr.substring(1));
			else
				dayOfMonthSet = Integer.parseInt(datemodstr);
		}
		String[] timeparts = parts[parts.length - 1].split(":");

		if(timeparts[0].startsWith("+"))
			hourOfDayInc = Integer.parseInt(timeparts[0].substring(1));
		else
			hourOfDaySet = Integer.parseInt(timeparts[0]);

		if(timeparts[1].startsWith("+"))
			minuteInc = Integer.parseInt(timeparts[1].substring(1));
		else
			minuteSet = Integer.parseInt(timeparts[1]);
	}

	@Override
	public long next(long millis)
	{
		GregorianCalendar gc = new GregorianCalendar(TimeZone.getDefault());
		gc.setTimeInMillis(millis);

		if(monthInc >= 0)
			gc.add(Calendar.MONTH, monthInc);

		if(monthSet >= 0)
			gc.set(Calendar.MONTH, monthSet);

		if(dayOfMonthInc >= 0)
			gc.add(Calendar.DAY_OF_MONTH, dayOfMonthInc);

		if(dayOfMonthSet >= 0)
			gc.set(Calendar.DAY_OF_MONTH, dayOfMonthSet);

		if(hourOfDayInc >= 0)
			gc.add(Calendar.HOUR_OF_DAY, hourOfDayInc);

		if(hourOfDaySet >= 0)
			gc.set(Calendar.HOUR_OF_DAY, hourOfDaySet);

		if(minuteInc >= 0)
			gc.add(Calendar.MINUTE, minuteInc);

		if(minuteSet >= 0)
			gc.set(Calendar.MINUTE, minuteSet);

		return gc.getTimeInMillis();
	}
}