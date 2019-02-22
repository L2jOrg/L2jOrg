package org.l2j.scripts.handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;

/**
 * @author Bonux
**/
public class WeeklyHunting extends DailyHunting
{
	private static final SchedulingPattern REUSE_PATTERN = new SchedulingPattern("30 6 * * 1");

	@Override
	public SchedulingPattern getReusePattern()
	{
		return REUSE_PATTERN;
	}
}
