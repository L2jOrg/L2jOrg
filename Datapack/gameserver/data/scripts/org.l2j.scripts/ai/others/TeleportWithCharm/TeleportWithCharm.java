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
package ai.others.TeleportWithCharm;

import ai.AbstractNpcAI;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * Charm teleport AI.<br>
 * @author Plim
 */
public final class TeleportWithCharm extends AbstractNpcAI
{
	// NPCs
	private static final int WHIRPY = 30540;
	private static final int TAMIL = 30576;
	// Items
	private static final int ORC_GATEKEEPER_CHARM = 1658;
	private static final int DWARF_GATEKEEPER_TOKEN = 1659;
	// Locations
	private static final Location ORC_TELEPORT = new Location(-80826, 149775, -3043);
	private static final Location DWARF_TELEPORT = new Location(-80826, 149775, -3043);
	
	private TeleportWithCharm()
	{
		addStartNpc(WHIRPY, TAMIL);
		addTalkId(WHIRPY, TAMIL);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		switch (npc.getId())
		{
			case WHIRPY:
			{
				if (hasQuestItems(player, DWARF_GATEKEEPER_TOKEN))
				{
					takeItems(player, DWARF_GATEKEEPER_TOKEN, 1);
					player.teleToLocation(DWARF_TELEPORT);
				}
				else
				{
					return "30540-01.htm";
				}
				break;
			}
			case TAMIL:
			{
				if (hasQuestItems(player, ORC_GATEKEEPER_CHARM))
				{
					takeItems(player, ORC_GATEKEEPER_CHARM, 1);
					player.teleToLocation(ORC_TELEPORT);
				}
				else
				{
					return "30576-01.htm";
				}
				break;
			}
		}
		return super.onTalk(npc, player);
	}
	
	public static AbstractNpcAI provider()
	{
		return new TeleportWithCharm();
	}
}
