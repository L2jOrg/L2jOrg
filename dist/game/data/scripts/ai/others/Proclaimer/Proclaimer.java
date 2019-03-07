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
package ai.others.Proclaimer;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.SkillHolder;
import com.l2jmobius.gameserver.model.skills.SkillCaster;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmobius.gameserver.network.serverpackets.NpcSay;

import ai.AbstractNpcAI;

/**
 * Proclaimer AI.
 * @author St3eT
 */
public final class Proclaimer extends AbstractNpcAI
{
	// NPCs
	private static final int[] PROCLAIMER =
	{
		36609, // Gludio
		36610, // Dion
		36611, // Giran
		36612, // Oren
		36613, // Aden
		36614, // Innadril
		36615, // Goddard
		36616, // Rune
		36617, // Schuttgart
	};
	// Skills
	private static final SkillHolder XP_BUFF = new SkillHolder(19036, 1); // Blessing of Light
	
	private Proclaimer()
	{
		addStartNpc(PROCLAIMER);
		addFirstTalkId(PROCLAIMER);
		addTalkId(PROCLAIMER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (!player.isOnDarkSide())
		{
			player.sendPacket(new NpcSay(npc.getObjectId(), ChatType.WHISPER, npc.getId(), NpcStringId.WHEN_THE_WORLD_PLUNGES_INTO_CHAOS_WE_WILL_NEED_YOUR_HELP_WE_HOPE_YOU_JOIN_US_WHEN_THE_TIME_COMES));
			
			final L2Clan ownerClan = npc.getCastle().getOwner();
			if (ownerClan != null)
			{
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player, "proclaimer.html"));
				packet.replace("%leaderName%", ownerClan.getLeaderName());
				packet.replace("%clanName%", ownerClan.getName());
				packet.replace("%castleName%", npc.getCastle().getName());
				player.sendPacket(packet);
			}
		}
		else
		{
			htmltext = "proclaimer-01.html";
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (event.equals("giveBuff"))
		{
			if (!player.isOnDarkSide())
			{
				SkillCaster.triggerCast(npc, player, XP_BUFF.getSkill());
			}
			else
			{
				htmltext = "proclaimer-01.html";
			}
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new Proclaimer();
	}
}