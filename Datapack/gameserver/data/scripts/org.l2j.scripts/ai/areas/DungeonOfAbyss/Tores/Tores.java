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
package ai.areas.DungeonOfAbyss.Tores;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QuangNguyen
 */
public class Tores extends AbstractNpcAI
{
	// NPC
	private static final int TORES = 31778;
	// Locations
	private static final Map<String, Location> LOCATIONS = new HashMap<>();
	static
	{
		// move from Tores
		LOCATIONS.put("1", new Location(-120325, -182444, -6752)); // Move to Magrit
		LOCATIONS.put("2", new Location(-109202, -180546, -6751)); // Move to Iris
	}
	
	private Tores()
	{
		addStartNpc(TORES);
		addTalkId(TORES);
		addFirstTalkId(TORES);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return "31778.htm";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "1":
			{
				final Location loc = LOCATIONS.get(event);
				if ((player.getLevel() > 39) && (player.getLevel() < 45))
				{
					player.teleToLocation(loc, true);
					
				}
				else
				{
					return "31778-no_level.htm";
				}
				break;
			}
			case "2":
			{
				final Location loc = LOCATIONS.get(event);
				if ((player.getLevel() > 44) && (player.getLevel() < 50))
				{
					player.teleToLocation(loc, true);
					
				}
				else
				{
					return "31778-no_level01.htm";
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Tores();
	}
}