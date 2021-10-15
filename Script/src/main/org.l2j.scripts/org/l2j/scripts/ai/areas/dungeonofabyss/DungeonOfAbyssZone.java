/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.ai.areas.dungeonofabyss;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneEngine;
import org.l2j.scripts.ai.AbstractNpcAI;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class DungeonOfAbyssZone extends AbstractNpcAI {

	private static final Zone[] ZONES = {
		ZoneEngine.getInstance().getZoneByName("The West Dungeon of Abyss"),
		ZoneEngine.getInstance().getZoneByName("The West Dungeon of Abyss 2nd"),
		ZoneEngine.getInstance().getZoneByName("The East Dungeon of Abyss"),
		ZoneEngine.getInstance().getZoneByName("The East Dungeon of Abyss 2nd"),

		ZoneEngine.getInstance().getZoneByName("The West Dungeon of Abyss Boss Room"),
		ZoneEngine.getInstance().getZoneByName("The West Dungeon of Abyss 2nd Boss Room"),
		ZoneEngine.getInstance().getZoneByName("The East Dungeon of Abyss Boss Room"),
		ZoneEngine.getInstance().getZoneByName("The East Dungeon of Abyss 2nd Boss Room")
	};

	private static final Location[] EXIT_LOCATIONS = {
		new Location(-120019, -182575, -6751), // Move to Magrit.
		new Location(-119977, -179753, -6751), // Move to Ingrit
		new Location(-109554, -180408, -6753), // Move to Iris
		new Location(-109595, -177560, -6753), // Move to Rosammy

		new Location(-120019, -182575, -6751), // Move to Magrit.
		new Location(-119977, -179753, -6751), // Move to Ingrit
		new Location(-109554, -180408, -6753), // Move to Iris
		new Location(-109595, -177560, -6753), // Move to Rosammy
	};

	private static final int EXIT_TIME = 60 * 60 * 1000; // 60 minute
	private static final int EXIT_TIME_BOSS_ROOM = 30 * 60 * 1000; // 60 minute
	public static final String EXIT_PLAYER = "EXIT_PLAYER";

	private DungeonOfAbyssZone() {
		var ids = new int[ZONES.length];
		for (int i = 0; i < ZONES.length; i++) {
			var zone = ZONES[i];
			if(zone != null) {
				ids[i] = zone.getId();
			}
		}
		addEnterZoneId(ids);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player) {
		if (event.startsWith(EXIT_PLAYER) && (player != null)) {
			for (int i = 0; i < ZONES.length; i++) {
				var zone = ZONES[i];
				if (zone != null && event.contains(zone.getName()) && zone.isCreatureInZone(player)) {
					player.teleToLocation(EXIT_LOCATIONS[i]);
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onEnterZone(Creature creature, Zone zone) {
		if (isPlayer(creature)) {
			final Player player = creature.getActingPlayer();

			for (Zone z : ZONES) {
				if(z != null) {
					cancelQuestTimer(EXIT_PLAYER + z.getName() + player.getObjectId(), null, player);
				}
			}

			startQuestTimer(EXIT_PLAYER + zone.getName() + player.getObjectId(), zone.getName().contains("boss") ? EXIT_TIME_BOSS_ROOM : EXIT_TIME, null, player);
		}
		return super.onEnterZone(creature, zone);
	}
	
	public static AbstractNpcAI provider()
	{
		return new DungeonOfAbyssZone();
	}
}
