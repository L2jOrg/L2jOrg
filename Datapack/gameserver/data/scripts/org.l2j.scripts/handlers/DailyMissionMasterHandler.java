package handlers;

import handlers.dailymissionhandlers.*;
import org.l2j.gameserver.handler.DailyMissionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class DailyMissionMasterHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DailyMissionMasterHandler.class);
	
	public static void main(String[] args) {
		DailyMissionHandler.getInstance().registerHandler("level", LevelDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("loginweekend", LoginWeekendDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("loginmonth", LoginMonthDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("quest", QuestDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("olympiad", OlympiadDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("siege", SiegeDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("ceremonyofchaos", CeremonyOfChaosDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("boss", BossDailyMissionHandler::new);
		DailyMissionHandler.getInstance().registerHandler("fishing", FishingDailyMissionHandler::new);
		LOGGER.info("Loaded {} handlers.", DailyMissionHandler.getInstance().size());
	}
}
