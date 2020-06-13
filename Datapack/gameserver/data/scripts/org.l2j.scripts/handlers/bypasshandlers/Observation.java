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
package handlers.bypasshandlers;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

public class Observation implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"observesiege",
		"observeoracle",
		"observe"
	};
	
	private static final int[][] LOCATIONS = new int[][]
	{
		//@formatter:off
		// Gludio
		{-18347, 114000, -2360, 500},
		{-18347, 113255, -2447, 500},
		// Dion
		{22321, 155785, -2604, 500},
		{22321, 156492, -2627, 500},
		// Giran
		{112000, 144864, -2445, 500},
		{112657, 144864, -2525, 500},
		// Innadril
		{116260, 244600, -775, 500},
		{116260, 245264, -721, 500},
		// Oren
		{78100, 36950, -2242, 500},
		{78744, 36950, -2244, 500},
		// Aden
		{147457, 9601, -233, 500},
		{147457, 8720, -252, 500},
		// Goddard
		{147542, -43543, -1328, 500},
		{147465, -45259, -1328, 500},
		// Rune
		{20598, -49113, -300, 500},
		{18702, -49150, -600, 500},
		// Schuttgart
		{77541, -147447, 353, 500},
		{77541, -149245, 353, 500},
		// Coliseum
		{148416, 46724, -3000, 80},
		{149500, 46724, -3000, 80},
		{150511, 46724, -3000, 80},
		// Dusk
		{-77200, 88500, -4800, 500},
		{-75320, 87135, -4800, 500},
		{-76840, 85770, -4800, 500},
		{-76840, 85770, -4800, 500},
		{-79950, 85165, -4800, 500},
		// Dawn
		{-79185, 112725, -4300, 500},
		{-76175, 113330, -4300, 500},
		{-74305, 111965, -4300, 500},
		{-75915, 110600, -4300, 500},
		{-78930, 110005, -4300, 500}
		//@formatter:on
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!(target instanceof org.l2j.gameserver.model.actor.instance.Observation))
		{
			return false;
		}
		
		if (player.hasSummon())
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_SERVITOR_SUMMONED);
			return false;
		}
		if (player.isOnEvent())
		{
			player.sendMessage("Cannot use while current Event");
			return false;
		}
		
		final String _command = command.split(" ")[0].toLowerCase();
		final int param;
		try
		{
			param = Integer.parseInt(command.split(" ")[1]);
		}
		catch (NumberFormatException nfe)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), nfe);
			return false;
		}
		
		if ((param < 0) || (param > (LOCATIONS.length - 1)))
		{
			return false;
		}
		final int[] locCost = LOCATIONS[param];
		
		final Location loc = new Location(locCost[0], locCost[1], locCost[2]);
		final long cost = locCost[3];
		
		switch (_command)
		{
			case "observesiege":
			{
				if (SiegeManager.getInstance().getSiege(loc) != null)
				{
					doObserve(player, (Npc) target, loc, cost);
				}
				else
				{
					player.sendPacket(SystemMessageId.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
				}
				return true;
			}
			case "observeoracle": // Oracle Dusk/Dawn
			{
				doObserve(player, (Npc) target, loc, cost);
				return true;
			}
			case "observe": // Observe
			{
				doObserve(player, (Npc) target, loc, cost);
				return true;
			}
		}
		return false;
	}
	
	private static void doObserve(Player player, Npc npc, Location pos, long cost)
	{
		if (player.reduceAdena("Broadcast", cost, npc, true))
		{
			// enter mode
			player.enterObserverMode(pos);
			player.sendItemList();
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
