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
package ai.others.CastleDoorManager;

import ai.AbstractNpcAI;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;

import java.util.StringTokenizer;

/**
 * Castle Door Manager AI.
 * @author St3eT
 */
public final class CastleDoorManager extends AbstractNpcAI
{
	// NPCs
	// @formatter:off
	private static final int[] DOORMENS_OUTTER =
	{
		35096, // Gludio
		35138, // Dion
		35180, // Giran
		35222, // Oren
		35267, // Aden
		35312, // Innadril
		35356, // Goddard
		35503, // Rune
		35548, // Schuttgart
	};
	private static final int[] DOORMENS_INNER =
	{
		35097, // Gludio
		35139, // Dion
		35181, // Giran
		35223, // Oren
		35268, 35269, 35270, 35271, // Aden
		35313, // Innadril
		35357, 35358, 35359, 35360, // Goddard
		35504, 35505, // Rune
		35549, 35550, 35551, 35552, // Schuttgart
	};
	// @formatter:on
	
	private CastleDoorManager()
	{
		addStartNpc(DOORMENS_OUTTER);
		addStartNpc(DOORMENS_INNER);
		addTalkId(DOORMENS_OUTTER);
		addTalkId(DOORMENS_INNER);
		addFirstTalkId(DOORMENS_OUTTER);
		addFirstTalkId(DOORMENS_INNER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final StringTokenizer st = new StringTokenizer(event, " ");
		final String action = st.nextToken();
		String htmltext = null;
		
		switch (action)
		{
			case "manageDoors":
			{
				if (isOwningClan(player, npc) && st.hasMoreTokens())
				{
					if (npc.getCastle().getSiege().isInProgress())
					{
						htmltext = "CastleDoorManager-siege.html";
					}
					else
					{
						final Castle castle = npc.getCastle();
						final boolean open = st.nextToken().equals("1");
						final String doorName1 = npc.getParameters().getString("DoorName1", null);
						final String doorName2 = npc.getParameters().getString("DoorName2", null);
						
						castle.openCloseDoor(player, doorName1, open);
						castle.openCloseDoor(player, doorName2, open);
					}
				}
				else
				{
					htmltext = getHtmlName(npc) + "-no.html";
				}
				break;
			}
			case "teleport":
			{
				if (isOwningClan(player, npc) && st.hasMoreTokens())
				{
					final int param = Integer.parseInt(st.nextToken());
					
					if (param == 1)
					{
						final int x = npc.getParameters().getInt("pos_x01");
						final int y = npc.getParameters().getInt("pos_y01");
						final int z = npc.getParameters().getInt("pos_z01");
						player.teleToLocation(x, y, z);
					}
					else
					{
						final int x = npc.getParameters().getInt("pos_x02");
						final int y = npc.getParameters().getInt("pos_y02");
						final int z = npc.getParameters().getInt("pos_z02");
						player.teleToLocation(x, y, z);
					}
				}
				else
				{
					htmltext = getHtmlName(npc) + "-no.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return isOwningClan(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR) ? getHtmlName(npc) + ".html" : getHtmlName(npc) + "-no.html";
	}
	
	private String getHtmlName(Npc npc)
	{
		return Util.contains(DOORMENS_INNER, npc.getId()) ? "CastleDoorManager-Inner" : "CastleDoorManager-Outter";
	}
	
	private boolean isOwningClan(Player player, Npc npc)
	{
		return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || ((npc.getCastle().getOwnerId() == player.getClanId()) && (player.getClanId() != 0));
	}
	
	public static AbstractNpcAI provider()
	{
		return new CastleDoorManager();
	}
}