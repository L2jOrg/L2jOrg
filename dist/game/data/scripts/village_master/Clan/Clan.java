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
package village_master.Clan;

import java.util.HashMap;
import java.util.Map;

import com.l2jmobius.gameserver.model.L2Clan;
import com.l2jmobius.gameserver.model.actor.L2Npc;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventType;
import com.l2jmobius.gameserver.model.events.ListenerRegisterType;
import com.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import com.l2jmobius.gameserver.model.events.annotations.RegisterType;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import com.l2jmobius.gameserver.model.skills.CommonSkill;

import ai.AbstractNpcAI;

/**
 * @author UnAfraid
 */
public final class Clan extends AbstractNpcAI
{
	// @formatter:off
	private static final int[] NPCS =
	{
		30026,30031,30037,30066,30070,30109,30115,30120,30154,30174,
		30175,30176,30187,30191,30195,30288,30289,30290,30297,30358,
		30373,30462,30474,30498,30499,30500,30503,30504,30505,30508,
		30511,30512,30513,30520,30525,30565,30594,30595,30676,30677,
		30681,30685,30687,30689,30694,30699,30704,30845,30847,30849,
		30854,30857,30862,30865,30894,30897,30900,30905,30910,30913,
	};
	// @formatter:on
	private static final Map<String, String> LEADER_REQUIRED = new HashMap<>();
	static
	{
		LEADER_REQUIRED.put("9000-03.htm", "9000-03-no.htm");
		LEADER_REQUIRED.put("9000-04.htm", "9000-04-no.htm");
		LEADER_REQUIRED.put("9000-05.htm", "9000-05-no.htm");
		LEADER_REQUIRED.put("9000-07.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-12a.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-12b.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-13a.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-13b.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-14a.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-14b.htm", "9000-07-no.htm");
		LEADER_REQUIRED.put("9000-15.htm", "9000-07-no.htm");
	}
	
	private Clan()
	{
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (LEADER_REQUIRED.containsKey(event))
		{
			if (!player.isClanLeader())
			{
				return LEADER_REQUIRED.get(event);
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		return "9000-01.htm";
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final L2PcInstance activeChar = event.getActiveChar();
		if (activeChar.isClanLeader())
		{
			final L2Clan clan = event.getActiveChar().getClan();
			clan.getMembers().forEach(member ->
			{
				if (member.isOnline())
				{
					CommonSkill.CLAN_ADVENT.getSkill().applyEffects(member.getPlayerInstance(), member.getPlayerInstance());
				}
			});
		}
		else if ((activeChar.getClan() != null) && activeChar.getClan().getLeader().isOnline())
		{
			CommonSkill.CLAN_ADVENT.getSkill().applyEffects(activeChar, activeChar);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogout(OnPlayerLogout event)
	{
		final L2PcInstance activeChar = event.getActiveChar();
		if (activeChar.isClanLeader())
		{
			final L2Clan clan = activeChar.getClan();
			clan.getMembers().forEach(member ->
			{
				if (member.isOnline())
				{
					member.getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
				}
			});
		}
		if (activeChar.getClan() != null)
		{
			activeChar.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onProfessionChange(OnPlayerProfessionChange event)
	{
		final L2PcInstance activeChar = event.getActiveChar();
		if (activeChar.isClanLeader() || ((activeChar.getClan() != null) && activeChar.getClan().getLeader().isOnline()))
		{
			CommonSkill.CLAN_ADVENT.getSkill().applyEffects(activeChar, activeChar);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CLAN_JOIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerClanJoin(OnPlayerClanJoin event)
	{
		final L2PcInstance activeChar = event.getActiveChar().getPlayerInstance();
		if (activeChar.getClan().getLeader().isOnline())
		{
			CommonSkill.CLAN_ADVENT.getSkill().applyEffects(activeChar, activeChar);
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_CLAN_LEFT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerClanLeft(OnPlayerClanLeft event)
	{
		event.getActiveChar().getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
	}
	
	public static void main(String[] args)
	{
		new Clan();
	}
}
