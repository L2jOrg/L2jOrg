package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.commons.time.cron.SchedulingPattern;
import org.l2j.gameserver.instancemanager.TrainingCampManager;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.tables.ClanTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
 **/
public class DailyTask extends AutomaticTask
{
	private static final Logger _log = LoggerFactory.getLogger(DailyTask.class);

	private static final SchedulingPattern PATTERN = new SchedulingPattern("30 6 * * *");

	public DailyTask()
	{
		super();
	}

	@Override
	public void doTask() throws Exception
	{
		_log.info("Daily Global Task: launched.");
		for(Player player : GameObjectsStorage.getPlayers())
			player.restartDailyCounters(false);
		ClanTable.getInstance().refreshClanAttendanceInfo();
		TrainingCampManager.getInstance().refreshTrainingCamp();
		_log.info("Daily Global Task: completed.");
	}

	@Override
	public long reCalcTime(boolean start)
	{
		return PATTERN.next(System.currentTimeMillis());
	}
}