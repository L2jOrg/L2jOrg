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
package org.l2j.scripts.ai.areas.DungeonOfAbyss.SoulTracker;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.scripts.ai.AbstractNpcAI;

import java.util.HashMap;
import java.util.Map;

/**
 * @author QuangNguyen
 */
public class Magrit extends AbstractNpcAI
{
	// NPC
	private static final int SOUL_TRACKER_MARGIT = 31774;
	// Item
	private static final int KEY_OF_WEST_WING = 90010;
	// Locations
	private static final Map<String, Location> LOCATIONS = new HashMap<>();
	static
	{
		LOCATIONS.put("1", new Location(-119440, -182464, -6752)); // Join Room from Magrit
		LOCATIONS.put("2", new Location(-120394, -179651, -6751)); // Move to West Wing 2nd
		LOCATIONS.put("3", new Location(-116963, -181492, -6575)); // Go to the Condemned of Abyss Prison
		LOCATIONS.put("4", new Location(146945, 26764, -2200)); // Return to Aden
	}
	
	private Magrit()
	{
		addStartNpc(SOUL_TRACKER_MARGIT);
		addTalkId(SOUL_TRACKER_MARGIT);
		addFirstTalkId(SOUL_TRACKER_MARGIT);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (npc.getId() == SOUL_TRACKER_MARGIT)
		{
			QuestState qs = player.getQuestState("Q00933_ExploringTheWestWingOfTheDungeonOfAbyss");
			switch (event)
			{
				case "1":
				{
					if ((qs != null) && qs.isStarted())
					{
						player.teleToLocation(LOCATIONS.get(event), false); // Join Room from Magrit
					}
					else
					{
						return "no_enter.htm";
					}
					break;
				}
				case "2":
				{
					player.teleToLocation(LOCATIONS.get(event), false); // Move to West Wing 2nd
					break;
				}
				case "3":
				{
					if (!hasQuestItems(player, KEY_OF_WEST_WING))
					{
						return "no_key.htm";
					}
					player.teleToLocation(LOCATIONS.get(event), false); // Go to the Condemned of Abyss Prison
					break;
				}
				case "4":
				{
					player.teleToLocation(LOCATIONS.get(event), false); // Return to Aden
					break;
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new Magrit();
	}
}