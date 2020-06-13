/*
 * Copyright © 2019 L2J Mobius
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
package ai.areas.DungeonOfAbyss;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Mobius
 */
public class DungeonOfAbyssZone extends AbstractNpcAI
{
	private static final Zone ABYSS_WEST_ZONE_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss");
	private static final Zone ABYSS_WEST_ZONE_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd");
	private static final Zone ABYSS_EAST_ZONE_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss");
	private static final Zone ABYSS_EAST_ZONE_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd");
	
	private static final Zone ABYSS_WEST_ZONE_BOSS_1 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss Boss Room");
	private static final Zone ABYSS_WEST_ZONE_BOSS_2 = ZoneManager.getInstance().getZoneByName("The West Dungeon of Abyss 2nd Boss Room");
	private static final Zone ABYSS_EAST_ZONE_BOSS_3 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss Boss Room");
	private static final Zone ABYSS_EAST_ZONE_BOSS_4 = ZoneManager.getInstance().getZoneByName("The East Dungeon of Abyss 2nd Boss Room");
	
	private static final int EXIT_TIME = 60 * 60 * 1000; // 60 minute
	private static final int EXIT_TIME_BOSS_ROOM = 30 * 60 * 1000; // 60 minute
	private static final Location EXIT_LOCATION_1 = new Location(-120019, -182575, -6751); // Move to Magrit
	private static final Location EXIT_LOCATION_2 = new Location(-119977, -179753, -6751); // Move to Ingrit
	private static final Location EXIT_LOCATION_3 = new Location(-109554, -180408, -6753); // Move to Iris
	private static final Location EXIT_LOCATION_4 = new Location(-109595, -177560, -6753); // Move to Rosammy
	
	private DungeonOfAbyssZone()
	{
		addEnterZoneId(ABYSS_WEST_ZONE_1.getId(), ABYSS_WEST_ZONE_2.getId(), ABYSS_EAST_ZONE_3.getId(), ABYSS_EAST_ZONE_4.getId(), ABYSS_WEST_ZONE_BOSS_1.getId(), ABYSS_WEST_ZONE_BOSS_2.getId(), ABYSS_EAST_ZONE_BOSS_3.getId(), ABYSS_EAST_ZONE_BOSS_4.getId());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("EXIT_PLAYER") && (player != null))
		{
			if (event.contains(ABYSS_WEST_ZONE_1.getName()) && ABYSS_WEST_ZONE_1.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_1);
			}
			else if (event.contains(ABYSS_WEST_ZONE_2.getName()) && ABYSS_WEST_ZONE_2.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_2);
			}
			else if (event.contains(ABYSS_EAST_ZONE_3.getName()) && ABYSS_EAST_ZONE_3.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_3);
			}
			else if (event.contains(ABYSS_EAST_ZONE_4.getName()) && ABYSS_EAST_ZONE_4.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_4);
			}
			else if (event.contains(ABYSS_WEST_ZONE_BOSS_1.getName()) && ABYSS_WEST_ZONE_BOSS_1.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_1);
			}
			else if (event.contains(ABYSS_WEST_ZONE_BOSS_2.getName()) && ABYSS_WEST_ZONE_BOSS_2.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_2);
			}
			else if (event.contains(ABYSS_EAST_ZONE_BOSS_3.getName()) && ABYSS_EAST_ZONE_BOSS_3.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_3);
			}
			else if (event.contains(ABYSS_EAST_ZONE_BOSS_4.getName()) && ABYSS_EAST_ZONE_BOSS_4.isCreatureInZone(player))
			{
				player.teleToLocation(EXIT_LOCATION_4);
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onEnterZone(Creature creature, Zone zone)
	{
		if (isPlayer(creature))
		{
			final Player player = creature.getActingPlayer();
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_1.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_2.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_3.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_4.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_BOSS_1.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_WEST_ZONE_BOSS_2.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_BOSS_3.getName() + player.getObjectId(), null, player);
			cancelQuestTimer("EXIT_PLAYER" + ABYSS_EAST_ZONE_BOSS_4.getName() + player.getObjectId(), null, player);
			startQuestTimer("EXIT_PLAYER" + zone.getName() + player.getObjectId(), zone.getName().contains("boss") ? EXIT_TIME_BOSS_ROOM : EXIT_TIME, null, player);
		}
		return super.onEnterZone(creature, zone);
	}
	
	public static AbstractNpcAI provider()
	{
		return new DungeonOfAbyssZone();
	}
}
