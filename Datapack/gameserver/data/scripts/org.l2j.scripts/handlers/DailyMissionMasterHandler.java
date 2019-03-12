package handlers;

import handlers.dailymissionhandlers.*;
import org.l2j.gameserver.handler.DailyMissionHandler;

import java.util.logging.Logger;

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
