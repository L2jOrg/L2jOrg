package l2s.gameserver.model.entity.olympiad;

import l2s.commons.threading.RunnableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeeklyTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

	@Override
	public void runImpl() throws Exception
	{
		Olympiad.doWeekTasks();
		_log.info("Olympiad System: Added weekly points to nobles.");
		Olympiad.setWeekStartTime(System.currentTimeMillis());
	}
}