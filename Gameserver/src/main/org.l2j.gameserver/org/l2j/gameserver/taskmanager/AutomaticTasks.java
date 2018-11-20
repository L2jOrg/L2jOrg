package org.l2j.gameserver.taskmanager;

import org.l2j.gameserver.taskmanager.tasks.PledgeHuntingSaveTask;
import org.l2j.gameserver.taskmanager.tasks.WeeklyTask;
import org.l2j.gameserver.taskmanager.tasks.DeleteExpiredMailTask;
import org.l2j.gameserver.taskmanager.tasks.DailyTask;
import org.l2j.gameserver.taskmanager.tasks.OlympiadSaveTask;
import org.l2j.gameserver.Config;

public class AutomaticTasks
{
	public static void init()
	{
		if(Config.ENABLE_OLYMPIAD)
			new OlympiadSaveTask();

		new DailyTask();
		new DeleteExpiredMailTask();
		new WeeklyTask();
		new PledgeHuntingSaveTask();
	}
}