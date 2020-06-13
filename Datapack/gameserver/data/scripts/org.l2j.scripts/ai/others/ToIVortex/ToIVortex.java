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
package ai.others.ToIVortex;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.CommonItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Tower of Insolence Vortex teleport AI.<br>
 * Updated to High Five by pmq.<br>
 * Reworked by xban1x.
 * @author Plim
 */
public final class ToIVortex extends AbstractNpcAI
{
	// NPCs
	private static final int KEPLON = 30949;
	private static final int EUCLIE = 30950;
	private static final int PITHGON = 30951;
	private static final int DIMENSION_VORTEX_1 = 30952;
	private static final int DIMENSION_VORTEX_2 = 30953;
	private static final int DIMENSION_VORTEX_3 = 30954;
	// Items
	private static final int GREEN_DIMENSION_STONE = 4404; // FIXME: 4401
	private static final int BLUE_DIMENSION_STONE = 4405; // FIXME: 4402
	private static final int RED_DIMENSION_STONE = 4406; // FIXME: 4403
	private static final Map<String, Integer> TOI_FLOOR_ITEMS = new HashMap<>();
	// Locations
	private static final Map<String, Location> TOI_FLOORS = new HashMap<>();
	// Misc
	private static final Map<String, Integer> DIMENSION_TRADE = new HashMap<>();
	static
	{
		TOI_FLOORS.put("1", new Location(114356, 13423, -5096));
		TOI_FLOORS.put("2", new Location(114666, 13380, -3608));
		TOI_FLOORS.put("3", new Location(111982, 16028, -2120));
		TOI_FLOORS.put("4", new Location(114636, 13413, -640));
		TOI_FLOORS.put("5", new Location(114152, 19902, 928));
		TOI_FLOORS.put("6", new Location(117131, 16044, 1944));
		TOI_FLOORS.put("7", new Location(113026, 17687, 2952));
		TOI_FLOORS.put("8", new Location(115571, 13723, 3960));
		TOI_FLOORS.put("9", new Location(114649, 14144, 4976));
		TOI_FLOORS.put("10", new Location(118507, 16605, 5984));
		
		TOI_FLOOR_ITEMS.put("1", GREEN_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("2", GREEN_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("3", GREEN_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("4", BLUE_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("5", BLUE_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("6", BLUE_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("7", RED_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("8", RED_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("9", RED_DIMENSION_STONE);
		TOI_FLOOR_ITEMS.put("10", RED_DIMENSION_STONE);
		
		DIMENSION_TRADE.put("GREEN", GREEN_DIMENSION_STONE);
		DIMENSION_TRADE.put("BLUE", BLUE_DIMENSION_STONE);
		DIMENSION_TRADE.put("RED", RED_DIMENSION_STONE);
	}
	
	private ToIVortex()
	{
		addStartNpc(KEPLON, EUCLIE, PITHGON, DIMENSION_VORTEX_1, DIMENSION_VORTEX_2, DIMENSION_VORTEX_3);
		addTalkId(KEPLON, EUCLIE, PITHGON, DIMENSION_VORTEX_1, DIMENSION_VORTEX_2, DIMENSION_VORTEX_3);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final int npcId = npc.getId();
		
		switch (event)
		{
			case "1":
			case "2":
			case "3":
			case "4":
			case "5":
			case "6":
			case "7":
			case "8":
			case "9":
			case "10":
			{
				final Location loc = TOI_FLOORS.get(event);
				final int itemId = TOI_FLOOR_ITEMS.get(event);
				if (hasQuestItems(player, itemId))
				{
					takeItems(player, itemId, 1);
					player.teleToLocation(loc, true);
				}
				else
				{
					return "no-stones.htm";
				}
				break;
			}
			case "GREEN":
			case "BLUE":
			case "RED":
			{
				if (player.getAdena() >= 100000)
				{
					takeItems(player, CommonItem.ADENA, 100000);
					giveItems(player, DIMENSION_TRADE.get(event), 1);
				}
				else
				{
					return npcId + "no-adena.htm";
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new ToIVortex();
	}
}
