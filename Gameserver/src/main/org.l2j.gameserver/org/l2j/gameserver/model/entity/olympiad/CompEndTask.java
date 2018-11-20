package org.l2j.gameserver.model.entity.olympiad;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.Announcements;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(CompEndTask.class);

	@Override
	public void runImpl() throws Exception
	{
		if(Olympiad.isOlympiadEnd())
			return;

		OlympiadManager manager = Olympiad._manager;

		// Если остались игры, ждем их завершения еще одну минуту
		if(manager != null && !manager.getOlympiadGames().isEmpty())
		{
			Olympiad.startCompEndTask(60000);
			return;
		}

		Olympiad._inCompPeriod = false;

		Announcements.announceToAll(SystemMsg.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM);

		_log.info("Olympiad System: Olympiad Game Ended");

		try
		{
			OlympiadDatabase.save();
		}
		catch(Exception e)
		{
			_log.error("Olympiad System: Failed to save Olympiad configuration:", e);
		}
		Olympiad.init();
	}
}