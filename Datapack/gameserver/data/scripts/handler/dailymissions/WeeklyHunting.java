package handler.dailymissions;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.listener.CharListener;
import org.l2j.gameserver.listener.actor.OnKillListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;

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
