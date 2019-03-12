package handlers;

import java.util.logging.Logger;

import org.l2j.gameserver.handler.DailyMissionHandler;

import handlers.dailymissionhandlers.BossDailyMissionHandler;
import handlers.dailymissionhandlers.CeremonyOfChaosDailyMissionHandler;
import handlers.dailymissionhandlers.FishingDailyMissionHandler;
import handlers.dailymissionhandlers.LevelDailyMissionHandler;
import handlers.dailymissionhandlers.OlympiadDailyMissionHandler;
import handlers.dailymissionhandlers.QuestDailyMissionHandler;
import handlers.dailymissionhandlers.SiegeDailyMissionHandler;

/**
 * @author UnAfraid
 */
public class DailyMissionMasterHandler
{
	private static final Logger LOGGER = Logger.getLogger(DailyMissionMasterHandler.class.getName());
	
	public static void main(String[] args)
	{
		DailyMissionHandler.getInstance().registerHandler("level", LevelDailyMissionHandler::new);
		// DailyMissionHandler.getInstance().registerHandler("loginAllWeek", LoginAllWeekDailyMissionHandler::new);
		// DailyMissionHandler.getInstance().registerHandler("loginAllMonth", LoginAllWeekDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("quest", QuestDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("olympiad", OlympiadDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("siege", SiegeDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("ceremonyofchaos", CeremonyOfChaosDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("boss", BossDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("fishing", FishingDailyMissionHandler::new);
		LOGGER.info(DailyMissionMasterHandler.class.getSimpleName() + ":  Loaded " + DailyMissionHandler.getInstance().size() + " handlers.");
	}
}
