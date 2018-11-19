package handler.dailymissions;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.gameserver.listener.CharListener;
import l2s.gameserver.listener.actor.OnKillListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;

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
