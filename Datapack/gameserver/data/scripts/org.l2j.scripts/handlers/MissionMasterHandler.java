/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
        engine.registerHandler("boss", BossMissionHandler::new);
        engine.registerHandler("fishing", FishingMissionHandler::new);
        engine.registerHandler("clan", ClanMissionHandler::new);
        engine.registerHandler("hunt", HuntMissionHandler::new);
        engine.registerHandler("spirit", SpiritMissionHandler::new);
		LOGGER.info("Loaded {} handlers.", engine.size());
	}
}
