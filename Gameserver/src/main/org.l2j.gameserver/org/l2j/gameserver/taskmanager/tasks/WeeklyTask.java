package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 **/
public class WeeklyTask extends AutomaticTask
{
	private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

	private static final SchedulingPattern PATTERN = new SchedulingPattern("30 6 * * 4");

	public WeeklyTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		_log.info("Weekly Global Task: launched.");
		for(Player player : GameObjectsStorage.getPlayers())
			player.restartWeeklyCounters(false);
		_log.info("Weekly Global Task: completed.");
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return PATTERN.next(System.currentTimeMillis());
	}
}