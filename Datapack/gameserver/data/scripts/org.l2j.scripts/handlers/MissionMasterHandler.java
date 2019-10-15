package handlers;

import handlers.mission.*;
import org.l2j.gameserver.engine.mission.MissionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author UnAfraid
 */
public class MissionMasterHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MissionMasterHandler.class);
	
	public static void main(String[] args) {
	    var engine = MissionEngine.getInstance();

        engine.registerHandler("level", LevelMissionHandler::new);
        engine.registerHandler("login", LoginMissionHandler::new);
        engine.registerHandler("quest", QuestMissionHandler::new);
        engine.registerHandler("olympiad", OlympiadMissionHandler::new);
        engine.registerHandler("siege", SiegeMissionHandler::new);
        engine.registerHandler("ceremonyofchaos", CeremonyOfChaosMissionHandler::new);
        engine.registerHandler("boss", BossMissionHandler::new);
        engine.registerHandler("fishing", FishingMissionHandler::new);
        engine.registerHandler("clan", ClanMissionHandler::new);
        engine.registerHandler("hunt", HuntMissionHandler::new);
        engine.registerHandler("spirit", SpiritMissionHandler::new);
		LOGGER.info("Loaded {} handlers.", engine.size());
	}
}
