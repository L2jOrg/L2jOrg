package l2s.gameserver.taskmanager;

import l2s.gameserver.taskmanager.tasks.PledgeHuntingSaveTask;
import l2s.gameserver.taskmanager.tasks.WeeklyTask;
import l2s.gameserver.taskmanager.tasks.DeleteExpiredMailTask;
import l2s.gameserver.taskmanager.tasks.DailyTask;
import l2s.gameserver.taskmanager.tasks.OlympiadSaveTask;
import l2s.gameserver.Config;

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