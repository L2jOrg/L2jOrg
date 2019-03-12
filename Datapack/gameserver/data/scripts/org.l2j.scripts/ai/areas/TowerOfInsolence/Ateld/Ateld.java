/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.areas.TowerOfInsolence.Ateld;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class Ateld extends AbstractNpcAI
{
	// NPC
	private static final int ATELD = 31714;
	// Location
	private static final Location TELEPORT_LOC = new Location(115322, 16756, 9012);
	
	private Ateld()
	{
		addFirstTalkId(ATELD);
		addTalkId(ATELD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("teleToBaium"))
		{
			if ((player.getCommandChannel() == null) || (player.getCommandChannel().getLeader() != player) || (player.getCommandChannel().getMemberCount() < 27) || (player.getCommandChannel().getMemberCount() > 300))
			{
				return "31714-01.html";
			}
			for (L2PcInstance member : player.getCommandChannel().getMembers())
			{
				if ((member != null) && (member.getLevel() > 70))
				{
					member.teleToLocation(TELEPORT_LOC);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return "31714.html";
	}
	
	public static void main(String[] args)
	{
		new Ateld();
	}
}
