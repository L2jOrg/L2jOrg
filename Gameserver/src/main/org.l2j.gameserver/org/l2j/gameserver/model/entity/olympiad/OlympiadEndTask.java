package org.l2j.gameserver.model.entity.olympiad;

import java.util.List;

import org.l2j.commons.threading.RunnableImpl;
import org.l2j.gameserver.Announcements;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.instancemanager.OlympiadHistoryManager;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.l2.s2c.SystemMessage;
import org.l2j.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadEndTask.class);

	@Override
	public void runImpl() throws Exception
	{
		if(Olympiad._inCompPeriod) // Если бои еще не закончились, откладываем окончание олимпиады на минуту
		{
            Olympiad.startOlympiadEndTask(60000);
			return;
		}

		Announcements.announceToAll(new SystemMessage(SystemMessage.OLYMPIAD_PERIOD_S1_HAS_ENDED).addNumber(Olympiad._currentCycle));
		//Announcements.announceToAll("Olympiad Validation Period has began");

		Olympiad._isOlympiadEnd = true;
		if(Olympiad._scheduledManagerTask != null)
			Olympiad._scheduledManagerTask.cancel(false);
		if(Olympiad._scheduledWeeklyTask != null)
			Olympiad._scheduledWeeklyTask.cancel(false);

        Olympiad.setValidationStartTime(Olympiad.getOlympiadPeriodEndTime());
		Olympiad._period = 1;

		Hero.getInstance().clearHeroes();
        OlympiadHistoryManager.getInstance().switchData();

        for(Player player : GameObjectsStorage.getPlayers())
            player.checkAndDeleteOlympiadItems();

        List<StatsSet> heroesToBe = OlympiadDatabase.computeHeroesToBe();
        if(Hero.getInstance().computeNewHeroes(heroesToBe))
            _log.warn("Olympiad: Error while computing new heroes!");

        OlympiadDatabase.cleanupParticipants();
        OlympiadDatabase.loadParticipantsRank();

		try
		{
			OlympiadDatabase.save();
		}
		catch(Exception e)
		{
			_log.error("Olympiad System: Failed to save Olympiad configuration!", e);
		}

        _log.info("Olympiad System: Starting Validation period. Time to end validation: " + Olympiad.getMillisToValidationEnd() / (60 * 1000) + " minutes");

		if(Olympiad._scheduledValdationTask != null)
			Olympiad._scheduledValdationTask.cancel(false);
		Olympiad._scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), Olympiad.getMillisToValidationEnd());
	}
}